package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.SiteDominate;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import us.codecraft.webmagic.Site;

public class OrderSiteSelector extends AbstractSiteSelector {

    public OrderSiteSelector(SiteDominate siteDominate) {
        super(siteDominate);
    }

    // 总是返回第一个
    @Override
    Site next(SiteListener siteListener) {
        return siteDominate.getSiteList().size() > 0 ? siteDominate.getSiteList().get(0) : null;
    }

    @Override
    boolean needWaitingWhenFrequent() {
        return true;
    }
}
