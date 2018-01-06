package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.crawler.SiteDominate;
import me.shenchao.webhunger.crawler.pipeline.StandalonePipeline;
import me.shenchao.webhunger.crawler.processor.WholeSiteCrawledProcessor;
import me.shenchao.webhunger.crawler.scheduler.QueueUrlScheduler;
import me.shenchao.webhunger.crawler.selector.OrderSiteSelector;
import me.shenchao.webhunger.entity.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class StandaloneController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneController.class);

    private SiteDominate siteDominate;

    StandaloneController(ControlConfig controlConfig) {
        super(controlConfig);
        init();
    }

    private void init() {
        // 启动单机版爬虫
        Spider spider = Spider.create(new WholeSiteCrawledProcessor());
        siteDominate = new SiteDominate(spider);
        spider.addPipeline(new StandalonePipeline());
        spider.setScheduler(new QueueUrlScheduler(new OrderSiteSelector(siteDominate)));
        // TODO 以后会动态变化
        spider.thread(5);
        spider.setExitWhenComplete(false);
        // 启动爬虫
        spider.runAsync();
    }

    /**
     * 在单机版中，直接通过方法调用，开始爬取站点
     */
    @Override
    void crawl(Host host) {
        siteDominate.start(host);
    }

    @Override
    void completed(Host host) {

    }

}
