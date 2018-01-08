package me.shenchao.webhunger.crawler.dominate;

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
    protected volatile List<Site> siteList = new LinkedList<>();

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    /**
     * 检查该站点是否已经爬取完毕
     * @param siteId siteId
     * @return 是否爬取完毕
     */
    public abstract boolean checkCrawledCompleted(String siteId);

}
