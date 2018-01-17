package me.shenchao.webhunger.crawler.listener;

import com.google.common.collect.Maps;
import me.shenchao.webhunger.crawler.util.HostSnapshotHelper;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
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
        ErrorPageDTO errorPage = new ErrorPageDTO(request.getSiteId(), request.getUrl(),
                request.getParentUrl(), request.getNowDepth(),
                (Integer) request.getExtra(Request.STATUS_CODE),
                (String) request.getExtra(Request.ERROR_MSG));
        statusStatistics.getErrorRequests().add(errorPage);
    }

    @Override
    public void onCompleted() {
        siteStatusStatisticsMap.clear();
        logger.warn("爬虫退出......");
    }

    @Override
    public HostCrawlingSnapshotDTO onCompleted(String siteId) {
        // 站点爬取完毕后，清除缓存
        SiteStatusStatistics eventualStatistics = siteStatusStatisticsMap.remove(siteId);
        return HostSnapshotHelper.create(siteId, eventualStatistics);
    }

    public SiteStatusStatistics getSiteStatusStatistics(String siteId) {
        return siteStatusStatisticsMap.computeIfAbsent(siteId, k -> new SiteStatusStatistics());
    }
}
