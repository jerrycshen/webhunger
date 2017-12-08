package me.shenchao.webhunger.client.crawler;

import me.shenchao.webhunger.client.api.crawler.UrlFilter;
import me.shenchao.webhunger.client.api.crawler.UrlFilterChain;
import me.shenchao.webhunger.client.crawler.util.UrlUtil;
import me.shenchao.webhunger.entity.PageInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

/**
 * 站点域控制,只爬取同一个域下的网页
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class SameDomainFilter implements UrlFilter {

    @Override
    public void doFilter(PageInfo page, Set<String> newUrls, UrlFilterChain filterChain) {
        Iterator<String> iterator = newUrls.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            try {
                if (!UrlUtil.getSecondLevelDomain(new URL(url).getHost().toLowerCase()).equals(page.getHost().getHostSecondDomain())) {
                    iterator.remove();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (newUrls.size() != 0) {
            filterChain.doFilter(page, newUrls);
        }
    }
}
