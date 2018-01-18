package me.shenchao.webhunger.crawler.scheduler;

import me.shenchao.webhunger.entity.webmagic.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.LifeCycle;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * base scheduler
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class BaseUrlScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(BaseUrlScheduler.class);

    @Override
    public void push(Request request, LifeCycle lifeCycle) {
        logger.trace("get a candidate url {}", request.getUrl());
        if (!isDuplicate(request, lifeCycle)) {
            logger.debug("push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request, lifeCycle);
        }
    }

    /**
     * 判断该请求是否已经爬取过
     * @param request request
     * @param lifecycle lifecycle
     * @return if duplicate return true
     */
    protected abstract boolean isDuplicate(Request request, LifeCycle lifecycle);

    /**
     * 添加待爬请求
     * @param request request ready to crawl
     * @param lifecycle lifeCycle
     */
    protected abstract void pushWhenNoDuplicate(Request request, LifeCycle lifecycle);
}
