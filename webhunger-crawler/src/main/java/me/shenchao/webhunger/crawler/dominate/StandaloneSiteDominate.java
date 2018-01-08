package me.shenchao.webhunger.crawler.dominate;

public class StandaloneSiteDominate extends BaseSiteDominate {

    @Override
    public boolean checkCrawledCompleted(String siteId) {
        return false;
    }
}
