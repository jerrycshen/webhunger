package me.shenchao.webhunger.crawler.scheduler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.crawler.selector.SiteSelector;
import me.shenchao.webhunger.entity.webmagic.Request;
import me.shenchao.webhunger.entity.webmagic.Site;
import us.codecraft.webmagic.LifeCycle;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 基于本地存储的URL调度器，每个站点使用FIFO队列管理URL顺序
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalQueueUrlScheduler extends DuplicateRemovedScheduler implements DuplicateRemover, SiteListener {

    private Map<String, BlockingQueue<Request>> queueMap = Maps.newConcurrentMap();

    private Map<String, Set<String>> duplicateMap = Maps.newConcurrentMap();

    private SiteSelector siteSelector;

    public LocalQueueUrlScheduler(SiteSelector siteSelector) {
        this.siteSelector = siteSelector;
        setDuplicateRemover(this);
    }

    /**
     * 创建站点队列只会在第一次添加种子URL的时候，所以创建操作并没有进行同步控制
     */
    @Override
    protected void pushWhenNoDuplicate(Request request, LifeCycle task) {
        queueMap.computeIfAbsent(request.getSiteId(), k -> new LinkedBlockingDeque<>()).add(request);
    }

    @Override
    public boolean isDuplicate(Request request, LifeCycle task) {
        return !duplicateMap.computeIfAbsent(request.getSiteId(), k -> Sets.newConcurrentHashSet()).add(request.getUrl());
    }

    @Override
    public void resetDuplicateCheck(LifeCycle task) {
    }

    @Override
    public int getTotalRequestsCount(LifeCycle task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLeftRequestsCount(String siteId) {
        return queueMap.get(siteId).size();
    }

    @Override
    public int getTotalRequestsCount(String siteId) {
        return duplicateMap.get(siteId).size();
    }

    @Override
    public Request poll(LifeCycle task) {
        Site site = siteSelector.select(this);
        return site == null ? null : queueMap.get(site.getHost().getHostId()).poll();
    }

}
