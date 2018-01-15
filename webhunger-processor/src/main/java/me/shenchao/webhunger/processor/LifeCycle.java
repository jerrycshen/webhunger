package me.shenchao.webhunger.processor;

import me.shenchao.webhunger.entity.Host;

import java.util.Map;

/**
 * 处理器生命周期接口
 *
 * @author Jerry Shen
 * @since  0.1
 */
public interface LifeCycle {

    /**
     * 正在爬取的所有站点集合
     *
     * @return sites
     */
    Map<String, Host> getSites();
}
