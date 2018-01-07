package us.codecraft.webmagic.scheduler;

import me.shenchao.webhunger.entity.webmagic.Request;
import us.codecraft.webmagic.LifeCycle;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Scheduler {

    /**
     * add a url to fetch
     *
     * @param request request
     * @param lifeCycle lifeCycle
     */
    public void push(Request request, LifeCycle lifeCycle);

    /**
     * get an url to crawl
     *
     * @param lifeCycle the task of spider
     * @return the url to crawl
     */
    public Request poll(LifeCycle lifeCycle);

}
