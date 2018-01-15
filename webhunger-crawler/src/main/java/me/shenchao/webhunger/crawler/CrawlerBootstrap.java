package me.shenchao.webhunger.crawler;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import me.shenchao.webhunger.config.CrawlerConfig;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.crawler.caller.LocalCrawlerCaller;
import me.shenchao.webhunger.crawler.dominate.BaseSiteDominate;
import me.shenchao.webhunger.crawler.dominate.DistributedSiteDominate;
import me.shenchao.webhunger.crawler.dominate.LocalSiteDominate;
import me.shenchao.webhunger.crawler.listener.BaseSpiderListener;
import me.shenchao.webhunger.crawler.listener.DistributedSpiderListener;
import me.shenchao.webhunger.crawler.listener.LocalSpiderListener;
import me.shenchao.webhunger.crawler.listener.SpiderListener;
import me.shenchao.webhunger.crawler.pipeline.DistributedPipeline;
import me.shenchao.webhunger.crawler.pipeline.LocalPipeline;
import me.shenchao.webhunger.crawler.processor.WholeSiteCrawledProcessor;
import me.shenchao.webhunger.crawler.caller.RpcCrawlerCaller;
import me.shenchao.webhunger.crawler.scheduler.LocalQueueUrlScheduler;
import me.shenchao.webhunger.crawler.scheduler.RedisQueueUrlScheduler;
import me.shenchao.webhunger.crawler.selector.RoundRobinSiteSelector;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import me.shenchao.webhunger.util.common.RedisUtils;
import me.shenchao.webhunger.util.common.SystemUtils;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 爬虫模块启动类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private CrawlerConfig crawlerConfig;

    private CrawlerCallable crawlerCaller;

    private void parseCrawlerConfig() {
        crawlerConfig = new CrawlerConfig();
        try {
            crawlerConfig.parse(SystemUtils.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (IOException e) {
            logger.error("爬虫模块配置文件读取失败，程序退出......", e);
            System.exit(1);
        }

        // log config info
        logger.info("配置解析完成，使用如下参数启动爬虫程序：");
        logger.info("Distributed: {}", crawlerConfig.isDistributed());
    }

    /**
     * 启动爬虫
     */
    public void start() {
        logger.info("爬虫节点正在启动......");
        // 解析配置
        parseCrawlerConfig();
        // 配置爬虫
        Spider spider = Spider.create(new WholeSiteCrawledProcessor());
        BaseSiteDominate siteDominate;
        BaseSpiderListener spiderListener;
        if (crawlerConfig.isDistributed()) {
            // 获取redis连接池
            JedisPool jedisPool = RedisUtils.initJedisPool(crawlerConfig.getRedisAddress());
            // 创建监听器
            spiderListener = new DistributedSpiderListener(jedisPool);
            // 启动zookeeper,注册本爬虫节点
            ZooKeeper zooKeeper = initZookeeper();
            // 创建分布式站点管理类
            siteDominate = new DistributedSiteDominate(zooKeeper, spider);
            // 创建爬虫控制类
            crawlerCaller = new RpcCrawlerCaller((DistributedSiteDominate) siteDominate, spiderListener,zooKeeper);
            // 启动dubbo，暴露接口与控制器RPC通信
            initDubbo(crawlerCaller);
            // 爬虫配置
            spider.addPipeline(new DistributedPipeline());
            spider.setScheduler(new RedisQueueUrlScheduler(new RoundRobinSiteSelector(siteDominate), jedisPool));
        } else {
            // 创建监听器
            spiderListener = new LocalSpiderListener(spider);
            siteDominate = new LocalSiteDominate(spider);
            crawlerCaller = new LocalCrawlerCaller((LocalSiteDominate) siteDominate, spiderListener);
            spider.addPipeline(new LocalPipeline());
            spider.setScheduler(new LocalQueueUrlScheduler(new RoundRobinSiteSelector(siteDominate)));
        }
        SpiderListener[] spiderListeners = {spiderListener};
        spider.setSpiderListeners(Arrays.asList(spiderListeners));
        spider.setSiteDominate(siteDominate);
        // TODO 以后会动态变化
        spider.thread(5);
        spider.setExitWhenComplete(false);
        // 启动爬虫
        spider.runAsync();
        logger.info("爬虫节点启动成功，等待调度运行......");
    }

    private ZooKeeper initZookeeper() {
        ZooKeeper zooKeeper = ZookeeperUtils.getZKConnection(crawlerConfig.getZkAddress());
        try {
            zooKeeper.create(ZookeeperPathConsts.getCrawlerNodePath(), "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            logger.info("Zookeeper连接成功......");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("启动失败：未能向Zookeeper注册本爬虫节点");
        }
        return zooKeeper;
    }

    private void initDubbo(CrawlerCallable crawlerCallable) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("Crawler");

        // 服务提供协议配置
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(20880);
        protocolConfig.setThreads(1);

        // 由于使用直连方式，所以不使用注册中心
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("N/A");

        // 服务配置
        ServiceConfig<CrawlerCallable> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setInterface(CrawlerCallable.class);
        serviceConfig.setRef(crawlerCallable);
        serviceConfig.setVersion("0.1");

        // 暴露服务
        serviceConfig.export();
        logger.info("Dubbo注册成功......");
    }

    public CrawlerCallable getCrawlerCaller() {
        return crawlerCaller;
    }

    public static void main(String[] args) {
        new CrawlerBootstrap().start();
    }
}
