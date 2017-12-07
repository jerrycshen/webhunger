package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.config.CrawlerConfig;
import me.shenchao.webhunger.crawler.pipeline.StandalonePipeline;
import me.shenchao.webhunger.crawler.processor.PageParser;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.exception.ConfigParseException;
import me.shenchao.webhunger.util.common.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.io.File;
import java.io.IOException;

/**
 * 爬虫模块启动类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private CrawlerConfig crawlerConfig;

    private SiteDominate siteDominate;

    private void parseCrawlerConfig() {
        crawlerConfig = new CrawlerConfig();
        try {
            crawlerConfig.parse(SystemUtil.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (ConfigParseException e) {
            logger.warn(e.toString());
        } catch (IOException e) {
            logger.error("爬虫模块配置文件读取失败，程序退出......", e);
            System.exit(1);
        }

        // log config info
        logger.info("配置解析完成，使用如下参数启动爬虫程序：");
        logger.info("Distributed: {}", crawlerConfig.isDistributed());
    }

    /**
     * 专门用于单机版调用，爬取新站点
     */
    public void crawl(Host host) {
        siteDominate.add(host);
    }

    /**
     * 启动爬虫
     */
    public void start() {
        logger.info("爬虫模块正在启动......");
        // 解析配置
        parseCrawlerConfig();
        // 配置爬虫
        Spider spider = Spider.create(new PageParser());
        // 启动站点管理类
        siteDominate = new SiteDominate(spider);
        if (crawlerConfig.isDistributed()) {
            // 启动zookeeper监听 TODO
            // 添加消息处理类
        } else {
            spider.addPipeline(new StandalonePipeline());
        }
        // TODO 以后会动态变化
        spider.thread(5);
        spider.setExitWhenComplete(false);
        // 启动爬虫
        spider.runAsync();
    }

    public static void main(String[] args) {
        new CrawlerBootstrap().start();
    }
}
