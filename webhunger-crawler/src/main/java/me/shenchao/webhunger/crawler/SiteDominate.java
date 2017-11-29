package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.entity.Host;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点管理类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class SiteDominate {

    private Spider spider;

    private Map<String, Site> siteMap = new HashMap<>();

    SiteDominate(Spider spider) {
        this.spider = spider;
        this.spider.setSiteDominate(this);
    }

    public void crawl(Host host) {
        Site site = Site.me();
        // TODO 睡眠时间应该动态变化，这里先写死
        site.setSleepTime(2000);
        site.setHost(host);
        siteMap.put(host.getHostId(), site);
        spider.addSeed(host.getHostIndex(), site);
        spider.signalNewUrl();
    }

    public void suspend(Host host) {

    }

    public void stop(Host host) {

    }

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }
}
