package me.shenchao.webhunger.crawler.filter;

import me.shenchao.webhunger.client.api.crawler.URLFilter;
import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Jerry Shen
 */
public class URLFilterChainImpl extends AbstractURLFilterChain {

    @Override
    public void doFilter(Page page, Site site, Set<String> newUrls) {
        if (threadLocal.get() == null) {
            threadLocal.set(filters.iterator());
        }
        Iterator<URLFilter> iterator = threadLocal.get();
        if (iterator.hasNext()) {
            URLFilter filter = iterator.next();
            filter.doFilter(page, site, newUrls, this);
        }
    }

}
