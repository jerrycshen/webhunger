package me.shenchao.webhunger.crawler.dominate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shenchao.webhunger.crawler.listener.SiteUrlNumListener;
import me.shenchao.webhunger.entity.webmagic.Request;
import me.shenchao.webhunger.entity.webmagic.Site;
import us.codecraft.webmagic.Spider;

import java.util.*;

/**
 * 页面爬取站点管理类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class BaseSiteDominate {

    /**
     * 记录站点ID与站点之间的映射关系
     */
    protected Map<String, Site> siteMap = Maps.newConcurrentMap();

    /**
     * 记录所有状态为state为Crawling的站点
     */
    protected volatile List<Site> siteList = Lists.newCopyOnWriteArrayList();

    protected Spider spider;

    public BaseSiteDominate(Spider spider) {
        this.spider = spider;
    }


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
     * 检查本爬虫节点当前是否在对该站点进行爬取
     * @param siteId siteId
     * @return 如果当前正在爬取返回true
     */
    public boolean isLocalCrawlingNow(String siteId) {
        // 获取当前spider正在爬取的请求
        Map<String, List<Request>> currentCrawlingRequests = spider.getCurrentCrawlingRequests();
        // 如果一次都没有爬取过
        return currentCrawlingRequests.get(siteId) == null || currentCrawlingRequests.get(siteId).size() != 0;
    }

    /**
     * 检查该站点是否已经爬取完毕
     * @param siteId siteId
     * @param siteListener siteListener
     * @return 是否爬取完毕
     */
    public abstract boolean checkCrawledCompleted(String siteId, SiteUrlNumListener siteListener);

    /**
     * 站点爬取结束回调方法
     * @param siteId siteId
     */
    void complete(String siteId) {
        // 彻底移除对该站点的所有缓存
        removeSite(siteId);
        // 移除正在爬取列表中的缓存
        spider.getCurrentCrawlingRequests().remove(siteId);
    }

    /**
     * 更新本地爬虫列表
     * @param newSiteList 新的爬取列表
     */
    public abstract void updateLocalCrawlingSiteList(List<Site> newSiteList);

}
