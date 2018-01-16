package me.shenchao.webhunger.crawler.filter;

import me.shenchao.webhunger.client.api.crawler.URLFilterChain;
import me.shenchao.webhunger.entity.webmagic.Page;
import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.Set;

/**
 * 使用包装设计模式，对URLFilterChainImpl进行包装<br>
 * 由于使用的是线程池，爬取线程在爬取完一个页面并解析之后，该线程将会被再次复用，所以ThreadLocal并不能保证每次过滤链都是从第一个过滤器开始，
 * 因此必须手动将其重置，也就是调用 {{@link ThreadLocal#remove()}}，此外该方法也防止了内存泄漏
 *
 * @author Jerry Shen
 * @since 3.0
 */
public class UrlFilterChainImplWrapper extends AbstractUrlFilterChain {

    private URLFilterChain urlFilterChain;

    public UrlFilterChainImplWrapper(URLFilterChain urlFilterChain) {
        this.urlFilterChain = urlFilterChain;
    }

    @Override
    public void doFilter(Page page, Site site, Set<String> newUrls) {
        urlFilterChain.doFilter(page, site, newUrls);
        // 重要的事情说三遍！！！  防止ThreadLocal内存泄漏 防止ThreadLocal内存泄漏 防止ThreadLocal内存泄漏
        threadLocal.remove();
    }

}
