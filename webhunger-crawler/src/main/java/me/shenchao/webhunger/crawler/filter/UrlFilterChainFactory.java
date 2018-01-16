package me.shenchao.webhunger.crawler.filter;

import me.shenchao.webhunger.client.api.crawler.URLFilter;
import me.shenchao.webhunger.client.api.crawler.URLFilterChain;
import me.shenchao.webhunger.crawler.filter.embed.FormatUrlsFilter;
import me.shenchao.webhunger.entity.UrlFilterConfig;
import me.shenchao.webhunger.util.classloader.ThirdPartyClassLoader;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * URL过滤链工厂
 *
 * TODO 资源回收
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class UrlFilterChainFactory {

    private static Map<String, URLFilterChain> urlFilterChainMap = new HashMap<>();

    /**
     * 根据站点ID获取指定站点的URL过滤链
     * @param site site
     * @return url filter chain
     */
    public static URLFilterChain getURLFilterChain(Site site) {
        String siteId = site.getHost().getHostId();
        if (urlFilterChainMap.get(siteId) == null) {
            // 创建过滤链，耗时操作
            URLFilterChain urlFilterChain = buildURLFilterChain(site);
            urlFilterChainMap.put(siteId, urlFilterChain);
            return urlFilterChain;
        } else {
            return urlFilterChainMap.get(siteId);
        }
    }

    private static URLFilterChain buildURLFilterChain(Site site) {
        UrlFilterChainImpl urlFilterChain = new UrlFilterChainImpl();
        // 添加内嵌的默认过滤器
        urlFilterChain.addFilter(new FormatUrlsFilter());
        // 添加自定义过滤器
        UrlFilterConfig urlFilterConfig = site.getHost().getHostConfig().getUrlFilterConfig();
        List<URLFilter> urlFilters = ThirdPartyClassLoader.loadClasses(urlFilterConfig.getUrlFilterJarDir(), urlFilterConfig.getFilterClassList(), URLFilter.class);
        for (URLFilter urlFilter : urlFilters) {
            urlFilterChain.addFilter(urlFilter);
        }
        return new UrlFilterChainImplWrapper(urlFilterChain);
    }

}
