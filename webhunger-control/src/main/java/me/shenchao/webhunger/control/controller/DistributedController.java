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
import java.util.List;

/**
 * Created on 2018-01-04
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DistributedController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(DistributedController.class);

    private ZooKeeper zooKeeper;

    DistributedController(ControlConfig controlConfig) {
        super(controlConfig);
        initZookeeper();
        initDubbo();
    }

    private void initDubbo() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("Controller");

        // 引用远程服务
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        ReferenceConfig<CrawlerCallable> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setInterface(CrawlerCallable.class);
        // TODO
        referenceConfig.setUrl("");
        // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
        CrawlerCallable crawlerCallable = referenceConfig.get();

    }

    private void initZookeeper() {
        // 获取zookeeper连接
        String zkServer = controlConfig.getZkServer();
        zooKeeper = ZookeeperUtils.getZKConnection(zkServer);
        logger.info("控制器连接Zookeeper成功......");
    }

    @Override
    void crawl(Host host) {

    }

    public List<Crawler> getOnlineCrawlers() {
        List<Crawler> crawlers = new ArrayList<>();
        try {
            List<String> list = zooKeeper.getChildren(ZookeeperPathConsts.CRAWLER, false);

            for (String nodeName : list) {
                String[] s = nodeName.split("@");
                Crawler crawler = new Crawler();
                crawler.setHostName(s[0]);
                crawler.setIp(s[1]);
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
