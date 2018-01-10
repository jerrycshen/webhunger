package me.shenchao.webhunger.crawler.listener;

import me.shenchao.webhunger.entity.webmagic.Request;

/**
 * 本地爬虫监听器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalSpiderListener extends BaseSpiderListener {

    @Override
    public void onSuccess(Request request) {
        super.onSuccess(request);
    }

    @Override
    public void onError(Request request) {
        super.onError(request);
    }
}
