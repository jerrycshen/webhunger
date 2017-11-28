package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.entity.Host;
import us.codecraft.webmagic.Spider;

/**
 * 爬虫模块启动类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerBootstrap {

    private Spider spider;

    public CrawlerBootstrap() {
        start();
    }

    /**
     * 加入待爬种子URL并开始爬取；此方法仅由单机版爬虫调用
     * @param host new host
     */
    public void crawl(Host host) {

    }

    /**
     * 启动爬虫
     */
    private void start() {
        // TODO START
    }

    public static void main(String[] args) {
        CrawlerBootstrap bootstrap = new CrawlerBootstrap();
    }
}
