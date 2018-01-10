package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.crawler.CrawlerBootstrap;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class LocalController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(LocalController.class);

    private CrawlerCallable crawlerController;

    LocalController(ControlConfig controlConfig) {
        super(controlConfig);
        initCrawler();
    }

    private void initCrawler() {
        CrawlerBootstrap crawlerBootstrap = new CrawlerBootstrap();
        crawlerBootstrap.start();
        this.crawlerController = crawlerBootstrap.getCrawlerCaller();
    }

    /**
     * 在单机版中，直接通过方法调用，开始爬取站点
     */
    @Override
    void crawl(Host host) {
        List<Host> hosts = new ArrayList<>(1);
        hosts.add(host);
        crawlerController.crawl(hosts);
    }

    @Override
    void crawlingCompleted(Host host) {

    }

    @Override
    void processingCompleted(Host host) {

    }

}
