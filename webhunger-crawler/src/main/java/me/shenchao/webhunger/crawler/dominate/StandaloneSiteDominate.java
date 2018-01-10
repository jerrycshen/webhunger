package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.crawler.listener.SiteListener;
import us.codecraft.webmagic.Spider;

/**
 * 单机版站点管理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class StandaloneSiteDominate extends BaseSiteDominate {

    public StandaloneSiteDominate(Spider spider) {
        super(spider);
    }

    @Override
    public boolean checkCrawledCompleted(String siteId, SiteListener siteListener) {
        if (checkLocalCrawledCompleted(siteId)) {
            removeSiteFromList(siteId);
            return true;
        }
        return false;
    }

    @Override
    void complete(String siteId) {

    }
}
