package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.SiteDominate;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;
import java.util.List;

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
                long isFrequent = site.isFrequent();
                // 如果过于频繁，则休眠一段时间再爬取
                if (isFrequent < 0) {
                    try {
                        Thread.sleep(isFrequent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                site.setLastCrawledTime(System.currentTimeMillis());
                return site;
            } else {
                // 站点队列为空，此爬虫结点对该站点爬取完毕
                // TODO 分布式环境需要向zookeeper更新站点信息

                // 爬取完毕，更新本结点站点管理器
                siteDominate.finish(site.getHost().getHostId());
                return null;
            }
        }
        return null;
    }

}
