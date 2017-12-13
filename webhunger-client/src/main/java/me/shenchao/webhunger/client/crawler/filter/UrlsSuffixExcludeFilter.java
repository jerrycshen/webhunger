package me.shenchao.webhunger.client.crawler.filter;

import me.shenchao.webhunger.client.api.crawler.URLFilter;
import me.shenchao.webhunger.client.api.crawler.URLFilterChain;
import me.shenchao.webhunger.client.crawler.util.SuffixInitialization;
import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

/**
 * 过滤掉URL后缀在后缀列表中的URL
 *
 * @author Jerry Shen
 * @since 3.0
 */
public class UrlsSuffixExcludeFilter implements URLFilter {

    @Override
    public void doFilter(Page page, Site site, Set<String> newUrls, URLFilterChain filterChain) {
        Iterator<String> iterator = newUrls.iterator();
        while (iterator.hasNext()) {
            String url_str = iterator.next();
            try {
                URL url = new URL(url_str);
                String path = url.getPath();
                String query = url.getQuery();
                if (isSuffixContained(path) || isSuffixContained(query)) {
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

    private boolean isSuffixContained(String str) {
        if (str == null)
            return false;
        int suffix_index = str.lastIndexOf(".");
        if (suffix_index != -1) {
            String suffix = str.substring(suffix_index + 1).toLowerCase();
            if (SuffixInitialization.getAll_filter_suffix().contains(suffix)) {
                return true;
            }
        }
        return false;
    }
}
