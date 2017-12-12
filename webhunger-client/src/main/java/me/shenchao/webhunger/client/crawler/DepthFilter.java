package me.shenchao.webhunger.client.crawler;

import me.shenchao.webhunger.client.api.crawler.URLFilter;
import me.shenchao.webhunger.client.api.crawler.URLFilterChain;
import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.Set;

/**
 * 深度控制过滤器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DepthFilter implements URLFilter {

    @Override
    public void doFilter(Page page, Site site, Set<String> newUrls, URLFilterChain filterChain) {
        int planDepth = site.getHost().getHostConfig().getDepth();
        // 如果深度要求不满足，无需再进行之后的过滤器，深度过滤器一般设置在过滤链之首，可以避免许多无用操作
        if (planDepth != -1 && page.getRequest().getNowDepth() >= planDepth) {
            // 此页面中的URL无需再抓取
            newUrls.clear();
            return;
        }
        filterChain.doFilter(page, site, newUrls);
    }
}
