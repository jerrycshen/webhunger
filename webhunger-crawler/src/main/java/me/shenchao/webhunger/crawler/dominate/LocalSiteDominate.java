package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * 单机版站点管理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalSiteDominate extends BaseSiteDominate {

    public LocalSiteDominate(Spider spider) {
        super(spider);
    }

    @Override
    public boolean checkCrawledCompleted(String siteId, SiteListener siteListener) {
        if (checkLocalCrawledCompleted(siteId)) {
            removeSiteFromList(siteId);
            complete(siteId);
            return true;
        }
        return false;
    }

    @Override
    void complete(String siteId) {
        // 彻底移除对该站点的所有缓存
        removeSite(siteId);
    }

    @Override
    public void updateLocalCrawlingSiteList(List<Site> newSiteList) {
        for (Site site : newSiteList) {
            addSite(site);
        }
        // 唤醒爬虫工作
        spider.signalNewUrl();
    }

    public void addSeedUrls(List<Site> newSiteList) {
        for (Site site : newSiteList) {
            spider.addSeed(site.getHost().getHostIndex(), site);
        }
    }
}
