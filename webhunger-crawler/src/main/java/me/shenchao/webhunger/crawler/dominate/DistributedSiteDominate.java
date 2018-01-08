package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.webmagic.Request;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分布式站点管理类
 *
 * @author  Jerry Shen
 * @since 0.1
 */
public class DistributedSiteDominate extends BaseSiteDominate {


    private ZooKeeper zooKeeper;

    private Spider spider;

    public DistributedSiteDominate(ZooKeeper zooKeeper, Spider spider) {
        this.zooKeeper = zooKeeper;
        this.spider = spider;
    }

    @Override
    public boolean checkCrawledCompleted(String siteId) {
        // 获取当前spider正在爬取的请求
        Map<String, List<Request>> currentCrawlingRequests = spider.getCurrentCrawlingRequests();
        // 如果当前有线程正在爬取该站点
        if (currentCrawlingRequests.get(siteId).size() > 0) {
            return false;
        } else {
            // 当前没有线程对该站点进行爬取，并且该站点对应的URL队列也为空，那么结束对该站点爬取
            // 在本地待爬列表中删除该站点, 需要注意的是 BaseSiteDominate.getSiteList()方法与本方法处于一个线程中，所以可以安全的对siteList进行删除操作
            Site crawledSite =siteMap.get(siteId);
            siteMap.remove(siteId);
            siteList.remove(crawledSite);

            // 更新zookeeper中该节点的值 + 1
            // 由于可能多个爬虫节点同时更新该值，所以这里使用分布式锁控制并发操作
            ZookeeperUtils.DistributedLock distributedLock = new ZookeeperUtils.DistributedLock(zooKeeper, ZookeeperPathConsts.LOCK);
            try {
                distributedLock.lock();
                try {
                    byte[] dataBytes = zooKeeper.getData(ZookeeperPathConsts.CRAWLING_HOST + "/" + siteId, false, null);
                    int value = Integer.parseInt(new String(dataBytes));
                    zooKeeper.setData(ZookeeperPathConsts.CRAWLING_HOST + "/" + siteId, String.valueOf(value + 1).getBytes(), -1);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                distributedLock.unlock();
            }
            return true;
        }
    }

    /**
     * 更新本地的爬取列表<br>
     * 按照业务逻辑，新的爬取列表与原爬取列表有两种可能差别情况：<br>
     *     1. 新的爬取列表包含了新的待爬站点，这是因为控制端加入了新站点进行爬取<br>
     *     2. 本地爬取列表数据领先于新的站点列表，这是因为某个站点爬取完毕了，控制端删除了该站点在zookeeper中的节点而触发的，而事实上
     *          本地列表早已将该站点删除<br>
     *
     *  所以综上所诉：新的站点列表长度一定大于等于本地站点列表，所以更新后，不会影响站点调度中getSiteList造成数组下标越界情况
     * @param newSiteList 新的爬取列表
     */
    public void updateLocalCrawlingSiteList(List<Site> newSiteList) {
        for (Site site : newSiteList) {
            String siteId = site.getHost().getHostId();
            if (siteMap.get(siteId) == null) {
                siteMap.put(siteId, site);
                siteList.add(site);
            }
        }
    }
}
