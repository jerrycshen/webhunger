package me.shenchao.webhunger.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * URL过滤链配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class URLFilterConfig {

    private String jarPath;

    /**
     * 注意filter的顺序，会按照顺序添加到过滤链
     */
    private List<String> filterClassList = new ArrayList<>();

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public List<String> getFilterClassList() {
        return filterClassList;
    }

    public void addFilterClass(String filterClass) {
        filterClassList.add(filterClass);
    }
}
