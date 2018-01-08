package me.shenchao.webhunger.crawler.rpc;

import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.crawler.dominate.DistributedSiteDominate;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫控制器，实现爬虫控制接口，接受控制模块RPC调用
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerController implements CrawlerCallable {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);

    private ZooKeeper zooKeeper;

    private DistributedSiteDominate siteDominate;

    private Spider spider;

    public CrawlerController(DistributedSiteDominate siteDominate, ZooKeeper zooKeeper, Spider spider) {
        this.siteDominate = siteDominate;
        this.zooKeeper = zooKeeper;
        this.spider = spider;
    }

    @Override
    public void run() {
        try {
            // 修改zookeeper中节点状态
            zooKeeper.setData(ZookeeperPathConsts.getCrawlerNodePath(), "1".getBytes(), -1);
            logger.info("爬虫节点开始运行......");
            // 监听待爬列表开始爬取操作
            watchCrawlingHostListChanged();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监控正在爬取的网站列表，一旦发生变化，同步更新本地爬取列表
     */
    private void watchCrawlingHostListChanged() {
        try {
            List<String> nodeList = zooKeeper.getChildren(ZookeeperPathConsts.DETAIL_HOST, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    watchCrawlingHostListChanged();
                }
            });
            List<Site> newSiteList = new ArrayList<>();
            for (String nodeName : nodeList) {
                String nodePath = ZookeeperPathConsts.DETAIL_HOST + "/" + nodeName;
                byte[] hostBytes = zooKeeper.getData(nodePath, false, null);
                Host host = JSON.parseObject(new String(hostBytes), Host.class);
                Site site = new Site(host);
                newSiteList.add(site);
            }
            siteDominate.updateLocalCrawlingSiteList(newSiteList);

            // 唤醒爬虫工作
            spider.signalNewUrl();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
