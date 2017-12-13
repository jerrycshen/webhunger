package me.shenchao.webhunger.client.crawler.filter;

import me.shenchao.webhunger.client.api.crawler.URLFilter;
import me.shenchao.webhunger.client.api.crawler.URLFilterChain;
import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.util.common.UrlUtils;

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
public class SameDomainFilter implements URLFilter {

    @Override
    public void doFilter(Page page, Site site, Set<String> newUrls, URLFilterChain filterChain) {
        Iterator<String> iterator = newUrls.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            try {
                if (!UrlUtils.getSecondLevelDomain(new URL(url).getHost().toLowerCase()).equals(site.getHost().getHostSecondDomain())) {
                    iterator.remove();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (newUrls.size() != 0) {
            filterChain.doFilter(page, site, newUrls);
        }
    }
}
