package me.shenchao.webhunger.client.crawler;

import me.shenchao.webhunger.client.api.crawler.UrlFilter;
import me.shenchao.webhunger.client.api.crawler.UrlFilterChain;
import me.shenchao.webhunger.client.crawler.util.UrlUtil;
import me.shenchao.webhunger.entity.PageInfo;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * FormatUrlsFilter提供如下功能：
 * <ul>
 *     <li>去除无效URL</li>
 *     <li>转换非标准字符</li>
 *     <li>标准格式化URL</li>
 * </ul>
 *
 * 这个过滤器有点特殊，它会替换待爬集合，把无效URL均过滤掉
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class FormatUrlsFilter implements UrlFilter {

    @Override
    public void doFilter(PageInfo page, Set<String> newUrls, UrlFilterChain filterChain) {
        // 1. 创建新的待爬集合
        Set<String> urls = new HashSet<>();

        // 2. 选取有效URL
        for (String url : newUrls) {
            // 2.1 剔除无效URL
            if (UrlUtil.isInValidUrl(url)) {
                continue;
            }
            // 2.2 移除URL锚节点
            url = UrlUtil.removeURLFragment(url);
            // 2.3 转换URL中非法字符
            url = UrlUtil.encodeIllegalCharacterInUrl(url);
            // 2.4 标准化URL
            URL urlClass = UrlUtil.canonicalizeUrl(url, page.getUrl());
            if (urlClass != null) {
                urls.add(urlClass.toExternalForm());
            }
        }
        // 3. 集合拷贝
        newUrls.clear();
        newUrls.addAll(urls);

        // 只有集合不为空才进行下一个过滤器
        if (newUrls.size() != 0) {
            // 下一个过滤器
            filterChain.doFilter(page, newUrls);
        }
    }
}
