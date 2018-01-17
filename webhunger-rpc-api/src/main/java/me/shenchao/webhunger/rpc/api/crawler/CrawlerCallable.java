package me.shenchao.webhunger.rpc.api.crawler;

import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
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

    /**
     * 判断在该爬虫节点对该站点的爬取是否结束
     * @param hostId hostId
     * @return if completed return true
     */
    HostCrawlingSnapshotDTO checkCrawledCompleted(String hostId);

    /**
     * 创建站点当前快照信息
     *
     * @param hostId hostId
     * @return 当前快照
     */
    HostCrawlingSnapshotDTO createSnapshot(String hostId);
}
