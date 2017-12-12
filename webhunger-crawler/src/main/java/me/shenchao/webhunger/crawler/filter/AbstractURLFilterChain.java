package me.shenchao.webhunger.crawler.filter;

import me.shenchao.webhunger.client.api.crawler.URLFilter;
import me.shenchao.webhunger.client.api.crawler.URLFilterChain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 过滤链通用操作
 *
 * @author Jerry Shen
 * @since 3.0
 */
public abstract class AbstractURLFilterChain implements URLFilterChain {

    List<URLFilter> filters = new ArrayList<>();

    /**
     * 使用ThreadLocal 解决过滤链复用<br>
     * 由于爬取过程使用线程池原因，仍然需要手动将其重置
     */
    static ThreadLocal<Iterator<URLFilter>> threadLocal = new ThreadLocal<Iterator<URLFilter>>() {
        @Override
        protected Iterator<URLFilter> initialValue() {
            return null;
        }
    };

    /**
     * 对外提供添加过滤器操作
     * @param filter filter
     */
    public AbstractURLFilterChain addFilter(URLFilter filter) {
        this.filters.add(filter);
        return this;
    }

}
