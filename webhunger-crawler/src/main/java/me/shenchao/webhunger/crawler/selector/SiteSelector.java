package me.shenchao.webhunger.crawler.selector;

import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.entity.webmagic.Site;

/**
 * 站点选择器，用于站点调度
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
    Site select(SiteListener siteListener);
}
