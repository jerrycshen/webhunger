package me.shenchao.webhunger.rpc.api.crawler;

import me.shenchao.webhunger.entity.Host;

import java.util.List;

/**
 * RPC接口，负责爬虫操作接口
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface CrawlerCallable {

    /**
     * 使爬虫节点状态从Ready转变为Running状态。爬虫之后会读取待爬列表开始爬取操作
     */
    void run();

    /**
     * 开始爬取该站点<br>
     *     <note>当前只有单机版爬虫有该方法实现</note>
     * @param hosts hosts
     */
    void crawl(List<Host> hosts);
}
