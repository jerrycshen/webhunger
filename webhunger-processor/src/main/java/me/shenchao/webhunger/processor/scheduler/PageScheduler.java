package me.shenchao.webhunger.processor.scheduler;

import me.shenchao.webhunger.dto.PageDTO;
import me.shenchao.webhunger.processor.LifeCycle;

/**
 * 页面调度器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface PageScheduler {

    /**
     * 获取下一个待处理Page
     * @param lifeCycle lifecycle
     * @return next page
     */
    PageDTO poll(LifeCycle lifeCycle);
}
