package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.entity.Host;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站点管理类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class SiteDominate {

    private Spider spider;

    private Map<String, Site> siteMap = new ConcurrentHashMap<>();

    private List<Site> siteList = Collections.synchronizedList(new LinkedList<>());

    SiteDominate(Spider spider) {
        this.spider = spider;
        this.spider.setSiteDominate(this);
    }

    void add(Host host) {
        Site site = Site.me();
        site.setHost(host);
        siteList.add(site);
        siteMap.put(host.getHostId(), site);
        spider.addSeed(host.getHostIndex(), site);
        spider.signalNewUrl();
    }

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }

    public List<Site> getSiteList() {
        return siteList;
    }
}
