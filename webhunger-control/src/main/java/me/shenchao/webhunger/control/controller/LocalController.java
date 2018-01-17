package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.crawler.CrawlerBootstrap;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostState;
import me.shenchao.webhunger.processor.Processor;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class LocalController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(LocalController.class);

    private CrawlerCallable crawlerCallable;

    private Processor processor;

    private ExecutorService hostProcessExecutor;

    LocalController(ControlConfig controlConfig) {
        super(controlConfig, false);
        init();
    }

    private void init() {
        CrawlerBootstrap crawlerBootstrap = new CrawlerBootstrap();
        crawlerBootstrap.start();
        this.crawlerCallable = crawlerBootstrap.getCrawlerCaller();
        processor = Processor.create();
        // 启动定时检查线程
        Thread thread = new Thread(new HostCrawledCompletedCheckThread());
        thread.setDaemon(true);
        thread.start();
        logger.info("启动站点爬取完成检测线程......");
        // 初始化站点处理线程池
        hostProcessExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public List<ErrorPageDTO> getErrorPageWhenCrawling(String hostId, int startPos, int size) {
        HostCrawlingSnapshotDTO currentHostCrawlingSnapshot = createCrawlingSnapshot(hostId);
        List<ErrorPageDTO> allErrorPages = currentHostCrawlingSnapshot.getErrorPages();
        return allErrorPages.subList(startPos, Math.min(startPos + size, allErrorPages.size()));
    }

    @Override
    public int getErrorPageNumWhenCrawling(String hostId) {
        HostCrawlingSnapshotDTO currentHostCrawlingSnapshot = createCrawlingSnapshot(hostId);
        List<ErrorPageDTO> allErrorPages = currentHostCrawlingSnapshot.getErrorPages();
        return allErrorPages.size();
    }

    @Override
    protected HostCrawlingSnapshotDTO createCrawlingSnapshot(String hostId) {
        return crawlerCallable.createSnapshot(hostId);
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
        controllerSupport.createSnapshot(host, HostState.Completed);
        logger.info("{} 页面处理完毕......", host.getHostName());
    }

    @Override
    void crawlingCompleted(Host host, HostCrawlingSnapshotDTO eventualSnapshot) {
        hostProcessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LocalController.super.crawlingCompleted(host, eventualSnapshot);
                /*
                 * 单机爬取中，由于爬取线程与页面处理线程在同一线程中，所以爬取结束也意味着所有页面处理结束，
                 * 接下来对站点全局做处理
                 */
                processor.processHost(host);
                processingCompleted(host);
            }
        });
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
                Map<Host, HostCrawlingSnapshotDTO> completedHosts = new HashMap<>();
                try {
                    for (Map.Entry<String, Host> entry : crawlingHostMap.entrySet()) {
                        HostCrawlingSnapshotDTO eventualSnapshot;
                        if ((eventualSnapshot = crawlerCallable.checkCrawledCompleted(entry.getKey())) != null) {
                            completedHosts.put(entry.getValue(), eventualSnapshot);
                        }
                    }
                } finally {
                    lock.unlock();
                }
                for (Map.Entry<Host, HostCrawlingSnapshotDTO> entry : completedHosts.entrySet()) {
                    crawlingCompleted(entry.getKey(), entry.getValue());
                }
                try {
                    Thread.sleep(CHECK_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
        }
    }

}
