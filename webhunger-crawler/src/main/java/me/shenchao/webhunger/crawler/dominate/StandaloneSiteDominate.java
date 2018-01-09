package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.crawler.listener.SiteListener;

public class StandaloneSiteDominate extends BaseSiteDominate {

    @Override
    public boolean checkCrawledCompleted(String siteId, SiteListener siteListener) {
        return false;
    }

    @Override
    void complete(String siteId) {

    }
}
