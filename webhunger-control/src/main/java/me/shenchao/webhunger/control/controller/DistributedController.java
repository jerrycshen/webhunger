package me.shenchao.webhunger.control.controller;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.Crawler;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ZooKeeper zooKeeper;

    private Map<String, ReferenceConfig<CrawlerCallable>> crawlerRPCMap = new HashMap<>();

    DistributedController(ControlConfig controlConfig) {
        super(controlConfig);
        initZookeeper();
    }

    private void initZookeeper() {
        // 获取zookeeper连接
        String zkServer = controlConfig.getZkServer();
        zooKeeper = ZookeeperUtils.getZKConnection(zkServer);
        logger.info("控制器连接Zookeeper成功......");
    }

    /**
     *
     * @param host host
     */
    @Override
    void crawl(Host host) {

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

    private void initDubbo(String crawlerIP) {
        if (crawlerRPCMap.get(crawlerIP) != null) {
            // 已存在连接
            return;
        }
        // todo 如果下线了，没有做资源清理操作，不知道dubbo内部是否相应断开机制

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

    /**
     * 获得所有在线的爬虫节点，包括正在爬取的和处在Ready状态的
     */
    public List<Crawler> getOnlineCrawlers() {
        List<Crawler> crawlers = new ArrayList<>();
        try {
            List<String> list = zooKeeper.getChildren(ZookeeperPathConsts.CRAWLER, false);

            for (String nodeName : list) {
                String[] s = nodeName.split("@");
                Crawler crawler = new Crawler();
                crawler.setHostName(s[0]);
                crawler.setIp(s[1]);

                // 与爬虫节点建立RPC连接
                initDubbo(s[1]);

                String nodePath = ZookeeperPathConsts.CRAWLER + "/" + nodeName;
                byte[] res = zooKeeper.getData(nodePath, false, null);
                crawler.setState(Crawler.State.valueOf(Integer.parseInt(new String(res))));
                crawlers.add(crawler);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return crawlers;
    }
}
