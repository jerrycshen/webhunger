package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
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

    /**
     * 分布式爬虫字段<br>
     * 站点爬取结束检测次数。如果<b>连续</b>检测{@code COMPLETED_CHECK_TIME}，则认定该站点在当前爬虫节点上
     * 爬取结束。否则，在还没有到达该临界值前，继续选择下一个站点进行爬取。如果某站点检测次数已经到达{@code COMPLETED_CHECK_TIME - i}
     * 次，但在下一次尝试获取该站点URL时，获取到了，那么会清空原来的计数，重新开始计数
     */
    private static final int COMPLETED_CHECK_TIME = 3;

    /**
     * 记录每个站点爬取结束检测次数
     */
    private Map<String, Integer> siteCompletedCheckCountMap = new HashMap<>();

    private ZooKeeper zooKeeper;

    public DistributedSiteDominate(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void checkCrawledCompleted(String siteId) {
        Integer checkedTime = siteCompletedCheckCountMap.putIfAbsent(siteId, 0);
        if (checkedTime < COMPLETED_CHECK_TIME) {
            siteCompletedCheckCountMap.put(siteId, checkedTime + 1);
        } else {
            // 已经到达检测次数临界值，可以认定该站点在本爬虫节点已经爬取完毕
            // 在本地待爬列表中删除该站点, 需要注意的是 BaseSiteDominate.getSiteList()方法与本方法处于一个线程中，所以可以安全的对siteList进行删除操作
            Site crawledSite =siteMap.get(siteId);
            siteMap.remove(siteId);
            siteList.remove(crawledSite);
            siteCompletedCheckCountMap.remove(siteId);

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
