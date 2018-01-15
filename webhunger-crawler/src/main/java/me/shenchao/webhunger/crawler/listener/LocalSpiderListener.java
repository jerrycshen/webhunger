package me.shenchao.webhunger.crawler.listener;

import me.shenchao.webhunger.entity.SiteStatusStatistics;
import me.shenchao.webhunger.entity.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 本地爬虫监听器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalSpiderListener extends BaseSpiderListener {

    private Spider spider;

    public LocalSpiderListener(Spider spider) {
        this.spider = spider;
    }

    @Override
    public void onSuccess(Request request) {
        super.onSuccess(request);
        SiteStatusStatistics siteStatusStatistics = getSiteStatusStatistics(request.getSiteId());
        siteStatusStatistics.setTotalPageNum(getTotalRequestsCount(request.getSiteId()));
        siteStatusStatistics.setLeftPageNum(getLeftRequestsCount(request.getSiteId()));
    }

    private int getLeftRequestsCount(String siteId) {
        return ((SiteUrlNumListener) spider.getScheduler()).getLeftRequestsNum(siteId);
    }

    private int getTotalRequestsCount(String siteId) {
        return ((SiteUrlNumListener) spider.getScheduler()).getTotalRequestsNum(siteId);
    }
}
