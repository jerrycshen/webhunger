package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.crawler.listener.SiteUrlNumListener;
import me.shenchao.webhunger.crawler.listener.SpiderListener;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分布式站点管理类
 *
 * @author  Jerry Shen
 * @since 0.1
 */
public class DistributedSiteDominate extends BaseSiteDominate {

    private ZooKeeper zooKeeper;

    private SiteCrawledCompletedCheckThread siteCrawledCompletedCheckThread;

    public DistributedSiteDominate(ZooKeeper zooKeeper, Spider spider, SpiderListener spiderListener) {
        super(spider, spiderListener);
        this.zooKeeper = zooKeeper;
        // 启动站点检查线程
        siteCrawledCompletedCheckThread = new SiteCrawledCompletedCheckThread();
        Thread thread = new Thread(siteCrawledCompletedCheckThread);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public boolean checkCrawledCompleted(String siteId, SiteUrlNumListener siteListener) {
        if (isLocalCrawlingNow(siteId)) {
            return false;
        } else {
            // 将该站点从待爬列表移除，但并没有真正移除该站点在map中的缓存，因为之后可能会恢复
            removeSiteFromList(siteId);
            /*
             * 考虑有多个爬虫节点存在并且只有一个待爬站点的情况，初始时，只有一个爬虫节点能获取种子URL，导致其他爬虫节点直接认为该站点已经爬取结束，造成最终
             * 只有一个爬虫节点对该站点进行爬取
             *
             * 解决方案：当本地检查发现对该站点已经爬取完成后，增加一个等待检查机制。
             *     1. 将每一个本地判断已经爬取完成的站点，加入到检测线程中去
             *     2. 检测线程定时扫描列表，在站点的timeout时间间隔内，判断每一个站点对应的URL是否有变化
             *     3. 如果URL仍旧是0，则认为该站点已经爬取完毕，那么进行删除
             *     4. 否则将该站点恢复到爬取状态
             */
            siteCrawledCompletedCheckThread.check(siteId, siteListener);
            return true;
        }
    }

    @Override
    void complete(String siteId) {
        super.complete(siteId);
        spiderListener.onCompleted(siteId);
        // 移除检测线程中map中的相关记录
        siteCrawledCompletedCheckThread.remove(siteId);

        // 更新zookeeper中该节点的值 + 1
        // 由于可能多个爬虫节点同时更新该值，所以这里使用分布式锁控制并发操作
        ZookeeperUtils.DistributedLock distributedLock = new ZookeeperUtils.DistributedLock(zooKeeper, ZookeeperPathConsts.CRAWLER_LOCK);
        distributedLock.lock();
        try {
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

    private void resumeSite(String siteId) {
        siteCrawledCompletedCheckThread.remove(siteId);
        addSiteToList(siteId);
        spider.signalNewUrl();
    }

    /**
     * 按照业务逻辑，新的爬取列表与原爬取列表有两种可能差别情况：<br>
     *     1. 新的爬取列表包含了新的待爬站点，这是因为控制端加入了新站点进行爬取<br>
     *     2. 本地爬取列表数据领先于新的站点列表，这是因为某个站点爬取完毕了，控制端删除了该站点在zookeeper中的节点而触发的，而事实上
     *          本地列表早已将该站点删除<br>
     *
     * @param newSiteList 新的爬取列表
     */
    @Override
    public void updateLocalCrawlingSiteList(List<Site> newSiteList) {
        for (Site site : newSiteList) {
            String siteId = site.getHost().getHostId();
            if (siteMap.get(siteId) == null) {
                addSite(site);
            }
        }
        // 唤醒爬虫工作
        spider.signalNewUrl();
    }

    /**
     * 检查站点是否爬取结束的线程
     *
     * @author Jerry Shen
     * @since 0.1
     */
    private class SiteCrawledCompletedCheckThread implements Runnable{

        /**
         * 检测时间间隔
         */
        private final long checkInterval = 3000;

        /**
         * 记录站点超时时刻。value为加入时刻 + site.timeout()
         */
        private Map<String, Long> checkTimeoutMap = new HashMap<>();

        /**
         * 记录站点监听器，用于获取各个站点的待爬URL数量
         */
        private Map<String, SiteUrlNumListener> siteListenerMap = new HashMap<>();

        private ReentrantLock lock = new ReentrantLock();

        @Override
        public void run() {
            while (true) {
                List<String> completedList = new ArrayList<>();
                List<String> resumeList = new ArrayList<>();
                lock.lock();
                try {
                    for (Map.Entry<String, Long> entry : checkTimeoutMap.entrySet()) {
                        int leftRequestNum = siteListenerMap.get(entry.getKey()).getLeftRequestsNum(entry.getKey());
                        // 如果该站点剩余URL数量为0，进一步检测是否过了timeout
                        if (leftRequestNum == 0) {
                            long currentTime = System.currentTimeMillis();
                            // 如果发现已经超过了timeout时间，表示可以认为本节点对该站点已经爬取完毕，那么加入结束列表
                            if (currentTime > entry.getValue()) {
                                completedList.add(entry.getKey());
                            }
                        } else {
                            // 发现了新URL，表明之前是假死，那么加入恢复队列
                            resumeList.add(entry.getKey());
                        }
                    }
                } finally {
                    lock.unlock();
                }
                for (String completedSiteId : completedList) {
                    complete(completedSiteId);
                }
                for (String resumeSiteId : resumeList) {
                    resumeSite(resumeSiteId);
                }
                try {
                    Thread.sleep(checkInterval);
                } catch (InterruptedException ignored) {}
            }
        }

        /**
         * 当站点在被本地爬取结束方法判定为结束后，会调用此方法，加入定时检查列表。此外该方法也会将该站点从本地待爬列表删除，此后
         * 如果发现该站点并没有真的结束，那么会重新加入到待爬列表
         *
         * @param siteId siteId
         * @param siteListener siteListener
         */
        private void check(String siteId, SiteUrlNumListener siteListener) {
            lock.lock();
            try {
                checkTimeoutMap.put(siteId, siteMap.get(siteId).getTimeOut() + System.currentTimeMillis());
                siteListenerMap.put(siteId, siteListener);
            } finally {
                lock.unlock();
            }
        }

        private void remove(String siteId) {
            lock.lock();
            try {
                checkTimeoutMap.remove(siteId);
                siteListenerMap.remove(siteId);
            } finally {
                lock.unlock();
            }
        }
    }
}
