package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.ZooKeeper;

/**
 * Created on 2018-01-04
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DistributedController extends MasterController {

    private ZooKeeper zooKeeper;

    DistributedController(ControlConfig controlConfig) {
        super(controlConfig);
        init();
    }

    private void init() {
        // 获取zookeeper连接
        String zkServer = controlConfig.getZkServer();
        zooKeeper = ZookeeperUtils.getZKConnection(zkServer);
    }

    @Override
    void crawl(Host host) {

    }
}
