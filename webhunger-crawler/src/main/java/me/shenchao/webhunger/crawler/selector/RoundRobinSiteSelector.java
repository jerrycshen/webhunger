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
        return select(siteListener, null);
    }

    private Site select(SiteListener siteListener, Site preSite) {
        Site nextSite;
        if ((nextSite = nextSite()) == null) {
            return null;
        }
        // 该站点剩余多少URL未爬取
        int leftRequestCount = siteListener.getLeftRequestsCount(nextSite.getHost().getHostId());
        if (leftRequestCount > 0) {
            long isFrequent;
            if ((isFrequent = nextSite.isFrequent()) > 0) {
                // 如果可以访问
                nextSite.setLastCrawledTime(System.currentTimeMillis());
                return nextSite;
            } else {
                // 如果访问过于频繁
                // 如果待爬站点只有一个，那么休眠后继续爬取，避免CPU空转，浪费资源
                if (preSite == nextSite) {
                    try {
                        Thread.sleep(-isFrequent + 30);
                    } catch (InterruptedException ignored) {}
                }
                select(siteListener, nextSite);
            }
        } else {
            // 如果待爬站点只有一个，休眠后继续判断，反正判断过快导致误判
            if (preSite == nextSite) {
                try {
                    Thread.sleep(nextSite.getSleepTime());
                } catch (InterruptedException ignored) {}
            }
            // 如果该站点待爬URL数量为0，控制权交还给SiteDominate决定站点是否爬取完毕
            siteDominate.checkCrawledCompleted(nextSite.getHost().getHostId());
            select(siteListener, nextSite);
        }
        return null;
    }
}
