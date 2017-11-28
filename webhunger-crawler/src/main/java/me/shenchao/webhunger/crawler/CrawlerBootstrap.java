package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.config.CrawlerConfig;
import me.shenchao.webhunger.crawler.pipeline.StandalonePipeline;
import me.shenchao.webhunger.crawler.processor.PageParser;
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
    }

    /**
     * 启动爬虫
     */
    public void start() {
        // 解析配置
        parseCrawlerConfig();
        logger.info("爬虫模块开始启动......");
        // 配置爬虫
        Spider spider = Spider.create(new PageParser());
        spider.setExitWhenComplete(false);
        if (crawlerConfig.isDistributed()) {

        } else {
            spider.addPipeline(new StandalonePipeline());
        }
        // 启动爬虫
        spider.run();
    }

    public static void main(String[] args) {
        new CrawlerBootstrap().start();
    }
}
