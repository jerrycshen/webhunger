package us.codecraft.webmagic.scheduler;

import us.codecraft.webmagic.LifeCycle;

/**
 * The scheduler whose requests can be counted for cn.edu.zju.eagle.monitor.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public interface MonitorableScheduler extends Scheduler {

    public int getLeftRequestsCount(LifeCycle task);

    public int getTotalRequestsCount(LifeCycle task);

}