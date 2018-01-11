package me.shenchao.webhunger.control.controller;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.constant.RedisPrefixConsts;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Crawler;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.webmagic.Request;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import me.shenchao.webhunger.util.common.MD5Utils;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2018-01-04
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DistributedController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(DistributedController.class);

    private Map<String, ReferenceConfig<CrawlerCallable>> crawlerRPCMap = new HashMap<>();

    private RedisSupport redisSupport = new RedisSupport();

    private DubboSupport dubboSupport = new DubboSupport();

    private ZookeeperSupport zookeeperSupport = new ZookeeperSupport();

    DistributedController(ControlConfig controlConfig) {
        super(controlConfig);
        // 监控所有爬虫节点
        zookeeperSupport.watchCrawlerStatus();
    }

    /**
     * 对站点开始爬取
     * @param host host
     */
    @Override
    void crawl(Host host) {
        // 1. 往redis对应站点队列添加种子URL
        redisSupport.push(host);
        // 2. 向zookeeper注册正在爬取站点
        zookeeperSupport.createHostNode(host, true);
    }

    @Override
    void crawlingCompleted(Host host) {
        releaseAfterCrawled(host);
        super.crawlingCompleted(host);
    }

    @Override
    void processingCompleted(Host host) {

    }

    /**
     * 分布式中创建站点快照相对耗时，因为要汇总所有节点该站点的状态
     *
     * @param hostId hostId
     * @return 站点当前快照
     */
    @Override
    protected HostCrawlingSnapshotDTO createCrawlingSnapshot(String hostId) {
        for (Map.Entry<String, ReferenceConfig<CrawlerCallable>> entry : crawlerRPCMap.entrySet()) {
            String crawlerIP = entry.getKey();
            // TODO
        }
        return null;
    }

    /**
     * 运行爬虫节点，因为一开始爬虫节点启动后，仍处于ready状态，等待调度使用
     *
     * @param crawlerIP 爬虫节点IP
     */
    public void runCrawler(String crawlerIP) {
        ReferenceConfig<CrawlerCallable> referenceConfig = crawlerRPCMap.get(crawlerIP);
        CrawlerCallable crawlerCallable = referenceConfig.get();
        crawlerCallable.run();
    }

    /**
     * 获得所有在线的爬虫节点，包括正在爬取的和处在Ready状态的
     */
    public List<Crawler> getOnlineCrawlers() {
        return zookeeperSupport.watchCrawlerStatus();
    }

    /**
     * 对爬取完毕后的站点进行资源清理
     * @param host host
     */
    private void releaseAfterCrawled(Host host) {
        // 清空Redis中缓存
        redisSupport.remove(host);
    }

    private void cleanAfterProcessed(Host host) {
    }

    /**
     * 进一步确认站点是否爬取完毕
     * @param host host
     */
    private void checkHostCrawlingCompleted(Host host) {
        // 先从zookeeper中删除该节点
        zookeeperSupport.deleteCrawlingHostNode(host);
        // 访问redis进一步确定站点URL队列为空
        if (redisSupport.getLeftRequestsCount(host) == 0) {
            crawlingCompleted(host);
        } else {
            /*
             * 如果站点队列中仍有URL未被爬取，那么重新将该站点加入到待爬列表，该方法中通过删除与添加操作，会
             * 触发各个爬虫节点更新待爬列表，从而重新加入该站点进行爬取
             */
            zookeeperSupport.createHostNode(host, false);
        }
    }

    private class ZookeeperSupport {

        private ZooKeeper zooKeeper;

        private volatile List<Crawler> onlineCrawlers = new ArrayList<>();

        private ZookeeperSupport() {
            // 获取zookeeper连接
            String zkServer = controlConfig.getZkAddress();
            zooKeeper = ZookeeperUtils.getZKConnection(zkServer);
            logger.info("控制器连接Zookeeper成功......");
        }

        private List<Crawler> getRunningCrawlers() {
            List<Crawler> runningCrawlers = new ArrayList<>();
            List<Crawler> temp = onlineCrawlers;
            for (Crawler crawler : temp) {
                if (crawler.getState() == Crawler.State.RUNNING.getValue()) {
                    runningCrawlers.add(crawler);
                }
            }
            return runningCrawlers;
        }

        private void createHostNode(Host host, boolean needCreateDetailHostNode) {
            try {
                // 创建站点详情节点
                String hostDetail = JSON.toJSONString(host);
                if (needCreateDetailHostNode) {
                    zooKeeper.create(getDetailHostNodePath(host), hostDetail.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                // 创建站点爬取统计节点
                zooKeeper.create(getCrawlingHostNodePath(host), "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                // 监控该站点的爬取状态
                zookeeperSupport.watchHostCrawlingStatus(host);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void deleteCrawlingHostNode(Host host) {
            try {
                zooKeeper.delete(getCrawlingHostNodePath(host), -1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }

        private List<Crawler> watchCrawlerStatus() {
            try {
                List<String> nodeList = zooKeeper.getChildren(ZookeeperPathConsts.CRAWLER, new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        watchCrawlerStatus();
                    }
                });
                List<Crawler> newOnlineCrawlers = new ArrayList<>();
                for (String nodeName : nodeList) {
                    String[] ss = nodeName.split("@");
                    Crawler crawler = new Crawler();
                    crawler.setHostName(ss[0]);
                    crawler.setIp(ss[1]);

                    String nodePath = ZookeeperPathConsts.CRAWLER + "/" + nodeName;
                    byte[] res = zooKeeper.getData(nodePath, false, null);
                    crawler.setState(Crawler.State.valueOf(Integer.parseInt(new String(res))));
                    newOnlineCrawlers.add(crawler);
                }
                this.onlineCrawlers = newOnlineCrawlers;
                // 检查RPC连接是否建立
                dubboSupport.checkRpcConnection();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return onlineCrawlers;
        }

        /**
         * 监控站点的爬取状态，判断站点是否已经爬取完毕，是否从正在爬取站点列表中删除该节点
         * @param host host
         */
        private void watchHostCrawlingStatus(Host host) {
            try {
                byte[] data = zooKeeper.getData(getCrawlingHostNodePath(host), new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        watchHostCrawlingStatus(host);
                    }
                }, null);
                int runningCrawlerNum = getRunningCrawlers().size();
                // 获取对该站点已经爬取完毕的数量
                int completedCrawlerNum = Integer.parseInt(new String(data));
                if (runningCrawlerNum == completedCrawlerNum) {
                    checkHostCrawlingCompleted(host);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private String getCrawlingHostNodePath(Host host) {
            return ZookeeperPathConsts.CRAWLING_HOST + "/" + host.getHostId();
        }

        private String getDetailHostNodePath(Host host) {
            return ZookeeperPathConsts.DETAIL_HOST + "/" + host.getHostId();
        }

    }

    /**
     * TODO 只考虑了建立连接，未考虑连接断开的情况，不清楚dubbo内部细节，不清楚是否会自动断开
     */
    private class DubboSupport {

        private void initDubbo(String crawlerIP) {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("Controller");

            // 引用远程服务
            // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
            ReferenceConfig<CrawlerCallable> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setInterface(CrawlerCallable.class);
            referenceConfig.setVersion("0.1");
            referenceConfig.setUrl(getDubboUrl(crawlerIP));

            crawlerRPCMap.put(crawlerIP, referenceConfig);
        }

        private String getDubboUrl(String crawlerIP) {
            return "dubbo://" + crawlerIP + ":20880";
        }

        private void checkRpcConnection() {
            List<Crawler> onlineCrawlers = zookeeperSupport.onlineCrawlers;
            for (Crawler crawler : onlineCrawlers) {
                // 如果还未建立RPC连接
                if (crawlerRPCMap.get(crawler.getIp()) == null) {
                    initDubbo(crawler.getIp());
                }
            }
        }
    }

    private class RedisSupport {

        private JedisPool pool;

        private RedisSupport() {
            String serverStr = controlConfig.getRedisAddress();
            String[] ss = serverStr.split(":");
            this.pool = new JedisPool(new JedisPoolConfig(), ss[0], Integer.parseInt(ss[1]));
        }

        private void push(Host host) {
            Jedis jedis = pool.getResource();
            try {
                jedis.rpush(RedisPrefixConsts.getQueueKey(host.getHostId()), host.getHostIndex());
                jedis.sadd(RedisPrefixConsts.getSetKey(host.getHostId()), host.getHostIndex());
                String field = MD5Utils.get16bitMD5(host.getHostIndex());
                Request request = new Request(host.getHostIndex());
                request.setSiteId(host.getHostId());
                request.setNowDepth(1);
                request.setParentUrl("");
                String value = JSON.toJSONString(request);
                jedis.hset(RedisPrefixConsts.getItemKey(host.getHostId()), field, value);
            } finally {
                pool.returnResource(jedis);
            }
        }

        private int getLeftRequestsCount(Host host) {
            Jedis jedis = pool.getResource();
            try {
                Long size = jedis.llen(RedisPrefixConsts.getQueueKey(host.getHostId()));
                return size.intValue();
            } finally {
                pool.returnResource(jedis);
            }
        }

        private void remove(Host host) {
            Jedis jedis = pool.getResource();
            try {
                String hostId = host.getHostId();
                jedis.del(RedisPrefixConsts.getQueueKey(hostId), RedisPrefixConsts.getSetKey(hostId), RedisPrefixConsts.getItemKey(hostId));
            } finally {
                pool.returnResource(jedis);
            }
        }
    }
}
