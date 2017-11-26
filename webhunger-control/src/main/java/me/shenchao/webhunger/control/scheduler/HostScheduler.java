package me.shenchao.webhunger.control.scheduler;

import me.shenchao.webhunger.entity.Host;

import java.util.List;

/**
 * 由于对于一批任务来说，与许多Host需要爬取，所以也就出现了站点调度这一概念。
 *
 * @see QueueHostScheduler
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface HostScheduler {

    /**
     * 添加一个待爬站点
     * @param host 待爬站点
     */
    void push(Host host);

    /**
     * 批量加入待爬站点
     * @param hosts 批量待爬站点
     */
    void pushAll(List<Host> hosts);

    /**
     * 获取一个站点进行爬取
     * @return host 站点
     */
    Host poll();
}
