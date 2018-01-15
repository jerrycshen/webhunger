package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.listener.SiteUrlNumListener;
import me.shenchao.webhunger.entity.webmagic.Site;

/**
 * 爬取模块站点选择器，选择哪个站点的URL去爬取
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface SiteSelector {

    /**
     * 获取下一个待爬站点
     * @param siteListener 站点监听器
     * @return next site to crawl
     */
    Site select(SiteUrlNumListener siteListener);
}
