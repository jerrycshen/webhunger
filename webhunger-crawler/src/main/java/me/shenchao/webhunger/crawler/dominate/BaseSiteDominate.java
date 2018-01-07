package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostState;
import me.shenchao.webhunger.entity.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站点管理类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class BaseSiteDominate {

    private Spider spider;

    /**
     * 记录站点ID与站点之间的映射关系，站点爬取完毕，不会立刻移除该项
     */
    private Map<String, Site> siteMap = new ConcurrentHashMap<>();

    /**
     * 列表中所有站点的state为Crawling 状态，一旦爬取完毕，立即从列表中删除
     */
    private List<Site> siteList = Collections.synchronizedList(new LinkedList<>());

    public BaseSiteDominate(Spider spider) {
        this.spider = spider;
        this.spider.setSiteDominate(this);
    }

    public void start(Host host) {
        Site site = Site.me();
        site.setHost(host);
        siteList.add(site);
        siteMap.put(host.getHostId(), site);
        spider.addSeed(host.getHostIndex(), site);
        spider.signalNewUrl();
    }

    /**
     * 当站点爬取完毕时候，在此爬虫结点的站点管理器中更新状态与清理操作
     * @param siteId siteId
     */
    public void finish(String siteId) {
        Site site = siteMap.get(siteId);
        // 更新站点状态
        site.getHost().setState(HostState.Processing);
        // 移除缓存记录
        siteList.removeIf(s -> s.getHost().getHostId().equals(siteId));
    }

    public boolean isSpiderActive() {
        return spider.getThreadAlive() > 0;
    }

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    /**
     * 检查该站点是否已经爬取完毕
     * @param siteId siteId
     */
    public abstract void checkCrawledCompleted(String siteId);
}
