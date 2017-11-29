package me.shenchao.webhunger.crawler.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 两个作用：<br>
 *    1. 提取新的URL，并交给URL过滤链处理，最后加入待爬列表
 *    2. 构造用于最后处理的PageInfo对象
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class PageParser implements PageProcessor {

    @Override
    public void process(Page page) {
        System.out.println(page.getRawText());
    }

}
