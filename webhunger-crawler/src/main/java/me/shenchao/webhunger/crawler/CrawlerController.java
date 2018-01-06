package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬虫控制器，实现爬虫控制接口，接受控制模块RPC调用
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerController implements CrawlerCallable {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    private ZooKeeper zooKeeper;

    CrawlerController(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void run() {
        // 修改zookeeper中节点状态
        try {
            zooKeeper.setData(ZookeeperPathConsts.getCrawlerNodePath(), "1".getBytes(), -1);
            logger.info("爬虫节点开始运行......");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 监听待爬列表开始爬取操作
    }
}
