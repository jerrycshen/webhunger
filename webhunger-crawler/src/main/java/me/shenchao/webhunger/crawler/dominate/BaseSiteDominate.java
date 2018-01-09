package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站点管理类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class BaseSiteDominate {

    /**
     * 记录站点ID与站点之间的映射关系
     */
    protected Map<String, Site> siteMap = new ConcurrentHashMap<>();

    /**
     * 列表中所有站点的state为Crawling 状态
     */
    protected volatile List<Site> siteList = Collections.synchronizedList(new LinkedList<>());

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    void addSite(Site site) {
        siteMap.put(site.getHost().getHostId(), site);
        siteList.add(site);
    }

    void addSiteToList(String siteId) {
        Site site = siteMap.get(siteId);
        siteList.add(site);
    }

    void removeSite(String siteId) {
        Site crawledSite = siteMap.get(siteId);
        siteMap.remove(siteId);
        siteList.remove(crawledSite);
    }

    void removeSiteFromList(String siteId) {
        Site site = siteMap.get(siteId);
        siteList.remove(site);
    }

    /**
     * 检查该站点是否已经爬取完毕
     * @param siteId siteId
     * @param siteListener siteListener
     * @return 是否爬取完毕
     */
    public abstract boolean checkCrawledCompleted(String siteId, SiteListener siteListener);

    /**
     * 站点爬取结束回调方法
     * @param siteId siteId
     */
    abstract void complete(String siteId);

}
