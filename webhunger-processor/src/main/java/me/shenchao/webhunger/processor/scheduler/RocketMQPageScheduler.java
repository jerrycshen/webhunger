package me.shenchao.webhunger.processor.scheduler;

import me.shenchao.webhunger.dto.PageDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.LifeCycle;
import me.shenchao.webhunger.processor.listener.HostPageNumListener;
import me.shenchao.webhunger.processor.selector.HostSelector;

/**
 * 基于RocketMQ消息队列的页面调度器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RocketMQPageScheduler implements PageScheduler, HostPageNumListener {

    private HostSelector hostSelector;

    public RocketMQPageScheduler(HostSelector hostSelector) {
        this.hostSelector = hostSelector;
    }

    @Override
    public PageDTO poll(LifeCycle lifeCycle) {
        Host host = hostSelector.select(this);
        return null;
    }

    @Override
    public int getLeftPagesNum(String hostId) {
        return 0;
    }
}
