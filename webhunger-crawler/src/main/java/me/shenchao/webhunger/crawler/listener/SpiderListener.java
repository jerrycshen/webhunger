package me.shenchao.webhunger.crawler.listener;

import me.shenchao.webhunger.entity.webmagic.Request;

/**
 * Listener of Spider, 原属于webmagic，为结构清晰，并与{@link SiteListener}  区别，移动到此包<br>
 *
 *     该接口用于爬虫节点在爬取过程中爬到的成功、错误进行操作
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SpiderListener {

    /**
     * 当爬取一个请求成功时，调用此方法
     * @param request request
     */
    void onSuccess(Request request);

    /**
     * 当爬取一个请求失败时，调用此方法
     * @param request
     */
    void onError(Request request);

    /**
     * 爬取完成时调用此方法
     */
    void onCompleted();
}
