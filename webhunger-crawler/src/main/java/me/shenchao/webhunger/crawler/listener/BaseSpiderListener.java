package me.shenchao.webhunger.crawler.listener;

import com.google.common.collect.Maps;
import me.shenchao.webhunger.entity.SiteStatusStatistics;
import me.shenchao.webhunger.entity.webmagic.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 通用实现
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class BaseSpiderListener implements SpiderListener {

    private static final Logger logger = LoggerFactory.getLogger(BaseSpiderListener.class);

    private Map<String, SiteStatusStatistics> siteStatusStatisticsMap = Maps.newConcurrentMap();

    @Override
    public void onSuccess(Request request) {
        SiteStatusStatistics statusStatistics = getSiteStatusStatistics(request);
        statusStatistics.getSuccessPageNum().incrementAndGet();
    }

    @Override
    public void onError(Request request) {
        SiteStatusStatistics statusStatistics = getSiteStatusStatistics(request);
        statusStatistics.getErrorPageNum().incrementAndGet();

    }

    private SiteStatusStatistics getSiteStatusStatistics(Request request) {
        return siteStatusStatisticsMap.computeIfAbsent(request.getSiteId(), k -> new SiteStatusStatistics());
    }

    @Override
    public void onCompleted() {
        siteStatusStatisticsMap.clear();
        logger.warn("爬虫退出......");
    }
}
