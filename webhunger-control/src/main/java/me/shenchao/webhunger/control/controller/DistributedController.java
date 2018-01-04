package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.Crawler;
import me.shenchao.webhunger.entity.Host;
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
        init();
    }

    private void init() {
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
