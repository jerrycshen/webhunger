package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.SiteDominate;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import us.codecraft.webmagic.Site;

public abstract class AbstractSiteSelector implements SiteSelector {

    SiteDominate siteDominate;

    AbstractSiteSelector(SiteDominate siteDominate) {
        this.siteDominate = siteDominate;
    }

    // 做最基本的时间 与 数量检测
    @Override
    public Site select(SiteListener siteListener) {
        Site nextSite = next(siteListener);
        while (nextSite != null) {
            // 如果太频繁抓取 或者 爬虫不在运行状态，则跳过

        }
        return nextSite;
    }

//    abstract Site get(SiteListener siteListener);

    abstract Site next(SiteListener siteListener);

    /**
     * 访问过于频繁时，是否需要等待该站点，还是爬取下一个站点
     * @return 默认不等待，尝试获取下一个站点
     */
    boolean needWaitingWhenFrequent() {
        return false;
    }
}
