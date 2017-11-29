package me.shenchao.webhunger.crawler.scheduler;

import us.codecraft.webmagic.LifeCycle;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用RoundRobin算法依次获取待爬URL
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RoundRobinScheduler implements Scheduler {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public void push(Request request, LifeCycle task) {

    }

    @Override
    public Request poll(LifeCycle task) {
        return null;
    }
}
