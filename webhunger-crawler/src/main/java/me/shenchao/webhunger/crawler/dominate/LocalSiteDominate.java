package me.shenchao.webhunger.crawler.dominate;

import com.google.common.collect.Maps;
import me.shenchao.webhunger.crawler.listener.BaseSpiderListener;
import me.shenchao.webhunger.crawler.listener.SiteUrlNumListener;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.Map;

/**
 * 单机版站点管理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalSiteDominate extends BaseSiteDominate {

    /**
     * 保存最终的站点爬取结果
     */
    private Map<String, HostCrawlingSnapshotDTO> eventualCrawlingSnapshotMap = Maps.newConcurrentMap();

    public LocalSiteDominate(Spider spider, BaseSpiderListener spiderListener) {
        super(spider, spiderListener);
    }

    @Override
    public boolean checkCrawledCompleted(String siteId, SiteUrlNumListener siteListener) {
        if (!isLocalCrawlingNow(siteId)) {
            complete(siteId);
            return true;
        }
        return false;
    }

    public HostCrawlingSnapshotDTO checkCrawledCompleted(String siteId) {
        return eventualCrawlingSnapshotMap.remove(siteId);
    }

    @Override
    void complete(String siteId) {
        super.complete(siteId);
        // 移除站点对应的相关URL队列
        spider.getScheduler().remove(siteId);
        // 移除监听器中缓存，并保存站点最终爬取结果
        HostCrawlingSnapshotDTO eventualSnapshot = spiderListener.onCompleted(siteId);
        eventualCrawlingSnapshotMap.put(siteId, eventualSnapshot);
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
