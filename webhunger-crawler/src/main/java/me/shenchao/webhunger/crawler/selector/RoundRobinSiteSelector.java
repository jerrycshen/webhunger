package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.SiteDominate;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinSiteSelector extends AbstractSiteSelector {

    private AtomicInteger index = new AtomicInteger(0);

    public RoundRobinSiteSelector(SiteDominate siteDominate) {
        super(siteDominate);
    }

    @Override
    Site next(SiteListener siteListener) {
        int pos = index.incrementAndGet() % siteDominate.getSiteList().size();
        return siteDominate.getSiteList().get(pos);
    }
}
