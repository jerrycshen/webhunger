package me.shenchao.webhunger.rpc.api.crawler;

/**
 * RPC接口，负责爬虫操作接口
 *
 * @since Jerry Shen
 * @since 0.1
 */
public interface CrawlerCallable {

    /**
     * 使爬虫节点状态从Ready转变为Running状态。爬虫之后会读取待爬列表开始爬取操作
     */
    void run();
}
