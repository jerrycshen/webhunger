package me.shenchao.webhunger.crawler.listener;

import me.shenchao.webhunger.entity.webmagic.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用实现
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class CommonSpiderListener implements SpiderListener {

    private static final Logger logger = LoggerFactory.getLogger(CommonSpiderListener.class);

    private Map<String, AtomicInteger> successCounterMap = new ConcurrentHashMap<>();

    private Map<String, AtomicInteger> errorCounterMap = new ConcurrentHashMap<>();

    @Override
    public void onSuccess(Request request) {
        AtomicInteger counter;
        if ((counter = successCounterMap.get(request.getSiteId())) == null) {
            counter = new AtomicInteger(0);
            successCounterMap.put(request.getSiteId(), counter);
        }
        counter.incrementAndGet();
    }

    @Override
    public void onError(Request request) {
        AtomicInteger counter;
        if ((counter = errorCounterMap.get(request.getSiteId())) == null) {
            counter = new AtomicInteger(0);
            errorCounterMap.put(request.getSiteId(), counter);
        }
        counter.incrementAndGet();
    }

    @Override
    public void onCompleted() {
        logger.warn("爬虫退出......");
    }
}
