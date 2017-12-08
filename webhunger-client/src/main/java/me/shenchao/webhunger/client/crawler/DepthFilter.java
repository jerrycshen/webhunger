package me.shenchao.webhunger.client.crawler;

import me.shenchao.webhunger.client.api.crawler.UrlFilter;
import me.shenchao.webhunger.client.api.crawler.UrlFilterChain;
import me.shenchao.webhunger.entity.PageInfo;

import java.util.Set;

/**
 * 深度控制过滤器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DepthFilter implements UrlFilter {

    /**
     * 默认全站爬取
     */
    private int planDepth = -1;

    public DepthFilter(int planDepth) {
        this.planDepth = planDepth;
    }

    @Override
    public void doFilter(PageInfo page, Set<String> newUrls, UrlFilterChain filterChain) {
        // 如果深度要求不满足，无需再进行之后的过滤器，深度过滤器一般设置在过滤链之首，可以避免许多无用操作
        if (planDepth != -1 && page.getDepth() >= planDepth) {
            // 此页面中的URL无需再抓取
            newUrls.clear();
            return;
        }
        filterChain.doFilter(page, newUrls);
    }

}
