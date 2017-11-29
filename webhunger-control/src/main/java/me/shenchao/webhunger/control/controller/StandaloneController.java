package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.crawler.CrawlerBootstrap;
import me.shenchao.webhunger.entity.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class StandaloneController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneController.class);

    private CrawlerBootstrap bootstrap;

    StandaloneController(ControlConfig controlConfig) {
        super(controlConfig);
        // 启动单机版爬虫
        bootstrap = new CrawlerBootstrap();
        bootstrap.start();
    }

    /**
     * 在单机版中，直接通过方法调用，开始爬取站点
     */
    @Override
    void crawl(Host host) {
        bootstrap.crawl(host);
    }

}
