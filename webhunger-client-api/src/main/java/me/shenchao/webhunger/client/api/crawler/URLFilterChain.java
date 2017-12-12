package me.shenchao.webhunger.client.api.crawler;

import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.Set;

/**
 * 过滤链接口，整套机制参考Tomcat，作了适当简化
 *
 * @see URLFilter
 *
 * @author Jerry Shen
 * @since 3.0
 */
public interface URLFilterChain {

    /**
     * @param page page
     * @param site site
     * @param newUrls 待爬URL集合
     */
    void doFilter(Page page, Site site, Set<String> newUrls);

}
