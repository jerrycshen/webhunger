package me.shenchao.webhunger.crawler.scheduler;

import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.crawler.selector.SiteSelector;
import us.codecraft.webmagic.LifeCycle;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Jerry Shen
 * @since 0.1
 */
public class QueueScheduler extends DuplicateRemovedScheduler implements DuplicateRemover, SiteListener {

    private Map<String, BlockingQueue<Request>> queueMap = new ConcurrentHashMap<>();

    private Map<String, Set<String>> duplicateMap = new ConcurrentHashMap<>();

    private SiteSelector siteSelector;

    public QueueScheduler(SiteSelector siteSelector) {
        this.siteSelector = siteSelector;
        setDuplicateRemover(this);
    }

    /**
     * 创建站点队列只会在第一次添加种子URL的时候，所以创建操作并没有进行同步控制
     */
    @Override
    protected void pushWhenNoDuplicate(Request request, LifeCycle task) {
        if (queueMap.get(request.getSiteId()) == null) {
            queueMap.put(request.getSiteId(), new LinkedBlockingDeque<>());
        }
        BlockingQueue<Request> queue = queueMap.get(request.getSiteId());
        queue.add(request);
    }

    @Override
    public boolean isDuplicate(Request request, LifeCycle task) {
        if (duplicateMap.get(request.getSiteId()) == null) {
            duplicateMap.put(request.getSiteId(), Collections.synchronizedSet(new HashSet<>()));
        }
        return !duplicateMap.get(request.getSiteId()).add(request.getUrl());
    }

    @Deprecated
    @Override
    public void resetDuplicateCheck(LifeCycle task) {
    }

    @Deprecated
    @Override
    public int getTotalRequestsCount(LifeCycle task) {
        return 0;
    }

    @Override
    public int getLeftRequestsCount(LifeCycle lifeCycle, String siteId) {
        return queueMap.get(siteId).size();
    }

    @Override
    public int getTotalRequestsCount(LifeCycle lifeCycle, String siteId) {
        return duplicateMap.get(siteId).size();
    }

    /**
     * 两轮检查
     */
    @Override
    public Request poll(LifeCycle task) {
        Site site = siteSelector.select(this);
        return site == null ? null : queueMap.get(site.getHost().getHostId()).poll();
    }

}
