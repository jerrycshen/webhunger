package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.crawler.CrawlerBootstrap;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class LocalController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(LocalController.class);

    private CrawlerCallable crawlerCallable;

    LocalController(ControlConfig controlConfig) {
        super(controlConfig, false);
        initCrawler();
    }

    private void initCrawler() {
        CrawlerBootstrap crawlerBootstrap = new CrawlerBootstrap();
        crawlerBootstrap.start();
        this.crawlerCallable = crawlerBootstrap.getCrawlerCaller();
        // 启动定时检查线程
        Thread thread = new Thread(new HostCrawledCompletedCheckThread());
        thread.setDaemon(true);
        thread.start();
        logger.info("启动站点爬取完成检测线程......");
    }

    /**
     * 在单机版中，直接通过方法调用，开始爬取站点
     */
    @Override
    void crawl(Host host) {
        List<Host> hosts = new ArrayList<>(1);
        hosts.add(host);
        crawlerCallable.crawl(hosts);
    }

    @Override
    void processingCompleted(Host host) {

    }

    @Override
    public List<ErrorPageDTO> getErrorPages(String hostId, int startPos, int size) {
        HostCrawlingSnapshotDTO currentHostCrawlingSnapshot = createCrawlingSnapshot(hostId);
        List<ErrorPageDTO> allErrorPages = currentHostCrawlingSnapshot.getErrorPages();
        return allErrorPages.subList(startPos, Math.min(startPos + size, allErrorPages.size()));
    }

    @Override
    public int getErrorPageNum(String hostId) {
        HostCrawlingSnapshotDTO currentHostCrawlingSnapshot = createCrawlingSnapshot(hostId);
        List<ErrorPageDTO> allErrorPages = currentHostCrawlingSnapshot.getErrorPages();
        return allErrorPages.size();
    }

    @Override
    protected HostCrawlingSnapshotDTO createCrawlingSnapshot(String hostId) {
        return crawlerCallable.createSnapshot(hostId);
    }

    /**
     * 站点爬取完成检测线程，定时检查站点是否爬取完毕<br>
     * 因为单机版中，控制模块与爬虫模块是单向引用关系，在爬虫模块中发现站点已经爬取完毕后，无法回调告诉控制
     * 模块站点已经爬取完成。
     * 所以通过定时检查站点快照的方式判断是否爬取完毕
     */
    private class HostCrawledCompletedCheckThread implements Runnable {

        private final Long CHECK_INTERVAL = 3000L;

        @Override
        public void run() {
            while (true) {
                lock.lock();
                List<Host> completedHosts = new ArrayList<>();
                try {
                    for (Map.Entry<String, Host> entry : crawlingHostMap.entrySet()) {
                        if (crawlerCallable.checkCrawledCompleted(entry.getKey())) {
                            completedHosts.add(entry.getValue());
                        }
                    }
                } finally {
                    lock.unlock();
                }
                for (Host host : completedHosts) {
                    crawlingCompleted(host);
                }
                try {
                    Thread.sleep(CHECK_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
        }
    }

}
