package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.SiteDominate;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.List;

/**
 * 顺序选择站点
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class OrderSiteSelector implements SiteSelector {

    private SiteDominate siteDominate;

    public OrderSiteSelector(SiteDominate siteDominate) {
        this.siteDominate = siteDominate;
    }

    @Override
    public Site select(SiteListener siteListener) {
        List<Site> siteList = siteDominate.getSiteList();
        if (siteList.size() > 0) {
            Site site = siteList.get(0);
            if (siteListener.getLeftRequestsCount(site.getHost().getHostId()) > 0) {
                long isFrequent;
                System.out.println(site.getSleepTime());
                while ((isFrequent = site.isFrequent()) < 0) {
                    try {
                        Thread.sleep(-isFrequent + 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                site.setLastCrawledTime(System.currentTimeMillis());
                return site;
            } else {
                // 站点队列为空，此爬虫结点对该站点爬取完毕
                // TODO 分布式环境需要向zookeeper更新站点信息

                // 判断当前爬虫中是否有活跃线程正在进行爬取
                if (!siteDominate.isSpiderActive()) {
                    // 如果没有，表示爬取完毕
                    siteDominate.finish(site.getHost().getHostId());
                }
                return null;
            }
        }
        return null;
    }

}
