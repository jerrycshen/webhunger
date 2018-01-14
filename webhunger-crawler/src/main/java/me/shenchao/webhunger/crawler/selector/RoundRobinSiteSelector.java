package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.dominate.BaseSiteDominate;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Round-Robin算法的站点选择器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RoundRobinSiteSelector implements SiteSelector {

    private BaseSiteDominate siteDominate;

    private AtomicInteger index = new AtomicInteger(0);

    /**
     * 上一个访问的站点
     */
    private Site prevSite = null;

    public RoundRobinSiteSelector(BaseSiteDominate siteDominate) {
        this.siteDominate = siteDominate;
    }

    private Site nextSite() {
        int crawlingHostNum = siteDominate.getSiteList().size();
        if (crawlingHostNum == 0) {
            return null;
        }
        int pos = index.incrementAndGet() % crawlingHostNum;
        return siteDominate.getSiteList().get(pos);
    }

    @Override
    public Site select(SiteListener siteListener) {
        Site nextSite;
        if ((nextSite = nextSite()) == null) {
            return null;
        }
        while (true) {
            if (nextSite == null) {
                return null;
            }
            // 该站点剩余多少URL未爬取
            int leftRequestCount = siteListener.getLeftRequestsCount(nextSite.getHost().getHostId());
            if (leftRequestCount > 0) {
                long isFrequent;
                if ((isFrequent = nextSite.isFrequent()) > 0) {
                    // 如果可以访问
                    nextSite.setLastCrawledTime(System.currentTimeMillis());
                    this.prevSite = nextSite;
                    return nextSite;
                } else {
                    // 如果访问过于频繁
                    // 如果待爬站点只有一个，那么休眠后继续爬取，避免CPU空转，浪费资源; 否则，跳过该站点，直接爬取下一站点
                    if (prevSite == nextSite) {
                        sleep(-isFrequent + 30);
                    } else {
                        // TODO 应该有更好的睡眠方法，在同时爬取的站点列表比较少的时候，可能导致所有站点过快访问，使得CPU空转，浪费资源，现在只是简单睡眠300毫秒
                        sleep(300);
                    }
                    this.prevSite = nextSite;
                    nextSite = nextSite();
                }
            } else {
                // 如果该站点待爬URL数量为0，控制权交还给SiteDominate决定站点是否爬取完毕
                boolean isCompleted = siteDominate.checkCrawledCompleted(nextSite.getHost().getHostId(), siteListener);
                // 如果对该站点的爬取还没有结束并且当前只有一个带爬取站点的时候，说明当前有其他线程正在对该站点进行爬取，所以睡眠一段时间后继续检测
                if (!isCompleted && prevSite == nextSite) {
                    sleep(nextSite.getSleepTime());
                }
                this.prevSite = nextSite;
                nextSite = nextSite();
            }
        }

    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {}
    }
}
