package me.shenchao.webhunger.processor.caller;

import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.dominate.BaseHostDominate;
import me.shenchao.webhunger.rpc.api.processor.ProcessorCallable;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面处理器控制器，实现页面处理控制接口，接受控制模块的RPC调用
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RpcProcessorCaller implements ProcessorCallable {

    private static final Logger logger = LoggerFactory.getLogger(RpcProcessorCaller.class);

    private ZooKeeper zooKeeper;

    private BaseHostDominate hostDominate;

    public RpcProcessorCaller(BaseHostDominate hostDominate, ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
        this.hostDominate = hostDominate;
    }

    @Override
    public void run() {
        try {
            zooKeeper.setData(ZookeeperPathConsts.getProcessorNodePath(), "1".getBytes(), -1);
            logger.info("页面处理节点开始运行......");
            // 监听待爬列表开始爬取操作
            watchProcessingHostListChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监控正在页面处理的网站列表，一旦发生变化，更新同步本地列表
     */
    private void watchProcessingHostListChanged() {
        try {
            List<String> nodeList = zooKeeper.getChildren(ZookeeperPathConsts.PROCESSING_HOST, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    watchProcessingHostListChanged();
                }
            });
            List<Host> newHostList = new ArrayList<>();
            for (String nodeName : nodeList) {
                String nodePath = ZookeeperPathConsts.DETAIL_HOST + "/" + nodeName;
                byte[] hostBytes = zooKeeper.getData(nodePath, false, null);
                Host host = JSON.parseObject(new String(hostBytes), Host.class);
                newHostList.add(host);
            }
            hostDominate.updateLocalProcessingHostList(newHostList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
