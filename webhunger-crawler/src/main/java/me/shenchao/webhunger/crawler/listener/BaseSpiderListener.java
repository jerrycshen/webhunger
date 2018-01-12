package me.shenchao.webhunger.crawler.listener;

import com.google.common.collect.Maps;
import me.shenchao.webhunger.dto.ErrorPageDTO;
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

    protected Map<String, SiteStatusStatistics> siteStatusStatisticsMap = Maps.newConcurrentMap();

    @Override
    public void onSuccess(Request request) {
        SiteStatusStatistics statusStatistics = getSiteStatusStatistics(request.getSiteId());
        statusStatistics.getSuccessPageNum().incrementAndGet();
    }

    @Override
    public void onError(Request request) {
        SiteStatusStatistics statusStatistics = getSiteStatusStatistics(request.getSiteId());
        statusStatistics.getErrorPageNum().incrementAndGet();
        ErrorPageDTO errorPage = new ErrorPageDTO.Builder(request.getSiteId(), request.getUrl())
                .depth(request.getNowDepth()).parentUrl(request.getParentUrl())
                .responseCode((Integer) request.getExtra(Request.STATUS_CODE))
                .errorMsg((String) request.getExtra(Request.ERROR_MSG)).build();
        statusStatistics.getErrorRequests().add(errorPage);
    }

    @Override
    public void onCompleted() {
        siteStatusStatisticsMap.clear();
        logger.warn("爬虫退出......");
    }

    public SiteStatusStatistics getSiteStatusStatistics(String siteId) {
        return siteStatusStatisticsMap.computeIfAbsent(siteId, k -> new SiteStatusStatistics());
    }
}
