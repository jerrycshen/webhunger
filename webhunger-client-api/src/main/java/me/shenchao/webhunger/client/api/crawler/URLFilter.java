package me.shenchao.webhunger.client.api.crawler;

import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.Set;

/**
 * 所有过滤器均需实现该接口，参考实现Tomcat#Filter
 *
 * @see URLFilterChain
 *
 * @author Jerry Shen
 * @since 3.0
 */
public interface URLFilter {

    /**
     * @param page 从当前页面中选取待爬URL
     * @param newUrls 待爬URL集合
     * @param filterChain 如果有多个过滤算法，实现者需要手动调用 {@link URLFilterChain#doFilter(Page, Site, Set)} )} 触发下一个过滤器
     */
    void doFilter(Page page, Site site, Set<String> newUrls, URLFilterChain filterChain);
}
