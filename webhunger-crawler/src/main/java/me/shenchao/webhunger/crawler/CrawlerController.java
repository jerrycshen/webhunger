package me.shenchao.webhunger.crawler;

import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;

/**
 * 爬虫控制器，实现爬虫控制接口，接受控制模块RPC调用
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CrawlerController implements CrawlerCallable {

    @Override
    public void run() {
        System.out.println(233);
    }
}
