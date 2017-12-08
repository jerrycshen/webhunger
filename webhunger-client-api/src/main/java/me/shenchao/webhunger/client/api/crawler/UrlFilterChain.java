package me.shenchao.webhunger.client.api.crawler;

import me.shenchao.webhunger.entity.PageInfo;

import java.util.Set;

/**
 * 过滤链接口，整套机制参考Tomcat，作了适当简化
 *
 * @see UrlFilter
 *
 * @author Jerry Shen
 * @since 3.0
 */
public interface UrlFilterChain {

    /**
     * @param page page
     * @param newUrls 待爬URL集合
     */
    void doFilter(PageInfo page, Set<String> newUrls);

}
