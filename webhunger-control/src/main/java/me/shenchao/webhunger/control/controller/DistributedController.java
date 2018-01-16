package me.shenchao.webhunger.control.controller;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.constant.RedisPrefixConsts;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.DistributedNode;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.webmagic.Request;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import me.shenchao.webhunger.rpc.api.processor.ProcessorCallable;
import me.shenchao.webhunger.util.common.MD5Utils;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;

/**
 * Created on 2018-01-04
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DistributedController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(DistributedController.class);

    private Map<String, ReferenceConfig<CrawlerCallable>> crawlerRPCMap = new HashMap<>();

    private Map<String, ReferenceConfig<ProcessorCallable>> processorRPCMap = new HashMap<>();

    private RedisSupport redisSupport = new RedisSupport();

    private DubboSupport dubboSupport = new DubboSupport();

    private ZookeeperSupport zookeeperSupport = new ZookeeperSupport();

    DistributedController(ControlConfig controlConfig) {
        super(controlConfig, true);
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

    /**
     * 分布式下，还要检查爬取节点、页面处理节点是否运行
     * @param host host
     * @return if pass the check return true
     */
    @Override
    boolean checkBeforeStart(Host host) {
        if (super.checkBeforeStart(host)) {

        }
        return false;
    }

    @Override
    void crawlingCompleted(Host host) {
        releaseAfterCrawled(host);
        super.crawlingCompleted(host);
    }

    @Override
    void processingCompleted(Host host) {

    }

    @Override
    public List<ErrorPageDTO> getErrorPages(String hostId, int startPos, int size) {
        return redisSupport.getErrorPages(hostId, startPos, startPos + size -1);
    }

    @Override
    public int getErrorPageNum(String hostId) {
        return redisSupport.getErrorPageNum(hostId);
    }

    @Override
    protected HostCrawlingSnapshotDTO createCrawlingSnapshot(String hostId) {
        return redisSupport.getCurrentCrawlingSnapshot(hostId);
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

    public void runProcessor(String processorIP) {
        ReferenceConfig<ProcessorCallable> referenceConfig = processorRPCMap.get(processorIP);
        ProcessorCallable processorCallable = referenceConfig.get();
        processorCallable.run();
    }

    /**
     * 获得所有在线的爬虫节点，包括正在爬取的和处在Ready状态的
     */
    public List<DistributedNode> getOnlineCrawlers() {
        return zookeeperSupport.getOnlineNodes(DistributedNode.NodeType.CRAWLER);
    }

    public List<DistributedNode> getOnlineProcessors() {
        return zookeeperSupport.getOnlineNodes(DistributedNode.NodeType.PROCESSOR);
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

    /**
     * 进一步确认站点是否已经爬取完毕、处理完毕
     * @param host host
     */
    private void checkHostProcessingCompleted(Host host) {
        // TODO
    }

    private class ZookeeperSupport {

        private ZooKeeper zooKeeper;

        private ZookeeperSupport() {
            // 获取zookeeper连接
            String zkServer = controlConfig.getZkAddress();
            zooKeeper = ZookeeperUtils.getZKConnection(zkServer);
            logger.info("控制器连接Zookeeper成功......");
        }

        /**
         * 获取指定节点下所有正在运行的节点列表
         * @param nodeType 节点类型
         */
        private List<DistributedNode> getRunningNodes(DistributedNode.NodeType nodeType) {
            List<DistributedNode> runningNodes = new ArrayList<>();
            List<DistributedNode> onlineNodes = getOnlineNodes(nodeType);
            for (DistributedNode onlineNode : onlineNodes) {
                if (onlineNode.getState() == DistributedNode.State.RUNNING.getValue()) {
                    runningNodes.add(onlineNode);
                }
            }
            return runningNodes;
        }

        private void createHostNode(Host host, boolean needCreateDetailHostNode) {
            try {
                // 创建站点详情节点
                String hostDetail = JSON.toJSONString(host);
                if (needCreateDetailHostNode) {
                    zooKeeper.create(getDetailHostNodePath(host), hostDetail.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                // 创建站点待爬取节点
                zooKeeper.create(getCrawlingHostNodePath(host), "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                // 创建站点待处理节点
                zooKeeper.create(getProcessingHostNodePath(host), "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                // 监控该站点的爬取状态
                zookeeperSupport.watchHostCrawlingStatus(host);
                // 监控该站点的页面处理状态
                zookeeperSupport.watchHostProcessingStatus(host);
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

        /**
         * 返回该节点下当前所有子节点
         * @param nodeType 节点类型
         * @return 该节点下当前所有的节点信息
         */
        private List<DistributedNode> getOnlineNodes(DistributedNode.NodeType nodeType) {
            List<DistributedNode> distributedNodes = new ArrayList<>();
            String nodePath = null;
            switch (nodeType) {
                case CRAWLER:
                    nodePath = ZookeeperPathConsts.CRAWLER;
                    break;
                case PROCESSOR:
                    nodePath = ZookeeperPathConsts.PROCESSOR;
                    break;
                default:
            }
            try {
                List<String> nodeList = zooKeeper.getChildren(nodePath, false);
                for (String nodeName : nodeList) {
                    String[] ss = nodeName.split("@");
                    DistributedNode distributedNode = new DistributedNode();
                    distributedNode.setHostName(ss[0]);
                    distributedNode.setIp(ss[1]);

                    String childPath = nodePath + "/" + nodeName;
                    byte[] res = zooKeeper.getData(childPath, false, null);
                    distributedNode.setState(DistributedNode.State.valueOf(Integer.parseInt(new String(res))));
                    distributedNodes.add(distributedNode);
                }
                // 检查RPC连接是否建立
                dubboSupport.checkRpcConnection(distributedNodes, nodeType);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return distributedNodes;
        }

        /**
         * 监控站点的页面处理状态，判断站点页面是否已经处理完毕，是否可以从该正在处理列表中删除该节点
         * @param host host
         */
        private synchronized void watchHostProcessingStatus(Host host) {
            try {
                byte[] data = zooKeeper.getData(getProcessingHostNodePath(host), new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        watchHostProcessingStatus(host);
                    }
                }, null);
                int state = Integer.parseInt(new String(data));
                if (state > 0) {
                    checkHostProcessingCompleted(host);
                }
            } catch (KeeperException.NoNodeException e){
                logger.debug("{} 页面处理完毕，该节点已经被删除......", host.getHostName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
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
                int runningCrawlerNum = getRunningNodes(DistributedNode.NodeType.CRAWLER).size();
                // 获取对该站点已经爬取完毕的数量
                int completedCrawlerNum = Integer.parseInt(new String(data));
                if (runningCrawlerNum == completedCrawlerNum) {
                    checkHostCrawlingCompleted(host);
                }
            } catch (KeeperException.NoNodeException e){
                logger.debug("{} 爬取完毕，该节点已经被删除......", host.getHostName());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private String getCrawlingHostNodePath(Host host) {
            return ZookeeperPathConsts.CRAWLING_HOST + "/" + host.getHostId();
        }

        private String getProcessingHostNodePath(Host host) {
            return ZookeeperPathConsts.PROCESSING_HOST + "/" + host.getHostId();
        }

        private String getDetailHostNodePath(Host host) {
            return ZookeeperPathConsts.DETAIL_HOST + "/" + host.getHostId();
        }

    }

    /**
     * TODO 只考虑了建立连接，未考虑连接断开的情况，不清楚dubbo内部细节，不清楚是否会自动断开
     */
    private class DubboSupport {

        private <T> void initDubbo(String crawlerIP, Map<String, ReferenceConfig<T>> rpcMap, Class<T> callableClass) {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("Controller");

            // 引用远程服务
            // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
            ReferenceConfig<T> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setInterface(callableClass);
            referenceConfig.setVersion("0.1");
            referenceConfig.setUrl(getDubboUrl(crawlerIP));

            rpcMap.put(crawlerIP, referenceConfig);
        }

        private String getDubboUrl(String crawlerIP) {
            return "dubbo://" + crawlerIP + ":20880";
        }

        private void checkRpcConnection(List<DistributedNode> onlineNodes, DistributedNode.NodeType nodeType) {
            switch (nodeType) {
                case CRAWLER:
                    for (DistributedNode onlineNode : onlineNodes) {
                        // 如果还未建立RPC连接
                        if (crawlerRPCMap.get(onlineNode.getIp()) == null) {
                            initDubbo(onlineNode.getIp(), crawlerRPCMap, CrawlerCallable.class);
                        }
                    }
                    break;
                case PROCESSOR:
                    for (DistributedNode onlineNode : onlineNodes) {
                        // 如果还未建立RPC连接
                        if (processorRPCMap.get(onlineNode.getIp()) == null) {
                            initDubbo(onlineNode.getIp(), processorRPCMap, ProcessorCallable.class);
                        }
                    }
                    break;
                default:
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

        private HostCrawlingSnapshotDTO getCurrentCrawlingSnapshot(String hostId) {
            Jedis jedis = pool.getResource();
            String successPageNum;
            String errorPageNum;
            Long totalPageNum;
            Long leftPageNum;
            try {
                Pipeline pipeline = jedis.pipelined();
                Response<String> successNumRes = pipeline.hget(RedisPrefixConsts.getCountKey(hostId), RedisPrefixConsts.COUNT_SUCCESS_NUM);
                Response<String> errorNumRes = pipeline.hget(RedisPrefixConsts.getCountKey(hostId), RedisPrefixConsts.COUNT_ERROR_NUM);
                Response<Long> totalNumRes = pipeline.scard(RedisPrefixConsts.getSetKey(hostId));
                Response<Long> leftNumRes = pipeline.llen(RedisPrefixConsts.getQueueKey(hostId));
                pipeline.sync();
                successPageNum = successNumRes.get();
                errorPageNum = errorNumRes.get();
                totalPageNum = totalNumRes.get();
                leftPageNum = leftNumRes.get();
            } finally {
                pool.returnResource(jedis);
            }
            return new HostCrawlingSnapshotDTO.Builder(hostId).successPageNum(Integer.parseInt(successPageNum))
                    .errorPageNum(Integer.parseInt(errorPageNum)).totalPageNum(totalPageNum.intValue())
                    .leftPageNum(leftPageNum.intValue()).build();
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

        private List<ErrorPageDTO> getErrorPages(String hostId, int startPos, int endPos) {
            Jedis jedis = pool.getResource();
            try {
                List<String> list = jedis.lrange(RedisPrefixConsts.getErrorPrefix(hostId), startPos, endPos);
                List<ErrorPageDTO> errorPages = new ArrayList<>(list.size());
                for (String errorPageStr : list) {
                    ErrorPageDTO errorPage = JSON.parseObject(errorPageStr, ErrorPageDTO.class);
                    errorPages.add(errorPage);
                }
                return errorPages;
            } finally {
                pool.returnResource(jedis);
            }
        }

        private int getErrorPageNum(String hostId) {
            Jedis jedis = pool.getResource();
            try {
                Long errorPageNum = jedis.llen(RedisPrefixConsts.getErrorPrefix(hostId));
                System.out.println(errorPageNum);
                return errorPageNum.intValue();
            } finally {
                pool.returnResource(jedis);
            }
        }
    }

}
