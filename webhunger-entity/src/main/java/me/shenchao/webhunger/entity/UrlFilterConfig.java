package me.shenchao.webhunger.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * URL过滤链配置
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class UrlFilterConfig {

    private String urlFilterJarDir;

    /**
     * 注意filter的顺序，会按照顺序添加到过滤链
     */
    private List<String> filterClassList = new ArrayList<>();

    public String getUrlFilterJarDir() {
        return urlFilterJarDir;
    }

    public void setUrlFilterJarDir(String urlFilterJarDir) {
        this.urlFilterJarDir = urlFilterJarDir;
    }

    public List<String> getFilterClassList() {
        return filterClassList;
    }

    public void addFilterClass(String filterClass) {
        filterClassList.add(filterClass);
    }
}
