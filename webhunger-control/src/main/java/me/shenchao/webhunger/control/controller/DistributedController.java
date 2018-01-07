package me.shenchao.webhunger.control.controller;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.constant.RedisPrefixConsts;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.Crawler;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
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
        zookeeperSupport.createHostNode(host);
    }

    @Override
    void completed(Host host) {
        System.out.println(host.getHostName() + "爬取完毕");
    }

    /**
     * 运行爬虫节点，因为一开始爬虫节点启动后，仍处于ready状态，等待调度使用
     *
     * @param crawlerIP 爬虫节点IP
     */
    public void run(String crawlerIP) {
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

        private void createHostNode(Host host) {
            try {
                // 创建站点详情节点
                String hostDetail = JSON.toJSONString(host);
                zooKeeper.create(getDetailHostNodePath(host), hostDetail.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
                    completed(host);
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

        public void push(Host host) {
            Jedis jedis = pool.getResource();
            try {
                jedis.rpush(RedisPrefixConsts.getQueueKey(host.getHostId()), host.getHostIndex());
                jedis.sadd(RedisPrefixConsts.getSetKey(host.getHostId()), host.getHostIndex());
            } finally {
                pool.returnResource(jedis);
            }
        }
    }
}
