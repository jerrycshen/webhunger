package me.shenchao.webhunger.client.crawler;

import me.shenchao.webhunger.client.api.crawler.UrlFilter;
import me.shenchao.webhunger.client.api.crawler.UrlFilterChain;
import me.shenchao.webhunger.client.crawler.util.SuffixInitialization;
import me.shenchao.webhunger.entity.PageInfo;

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
public class UrlsSuffixExcludeFilter implements UrlFilter {

    @Override
    public void doFilter(PageInfo page, Set<String> newUrls, UrlFilterChain filterChain) {
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
            filterChain.doFilter(page, newUrls);
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
