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

    /**
     * 爬取完毕后删除该站点的缓存
     * @param siteId crawled site's id
     */
    default void remove(String siteId) {

    }

    /**
     * 对该站点待爬URL队列进行清空操作，实现时需要注意某运行中的爬取线程加入新的待爬URL
     * @param siteId siteId
     */
    default void clear(String siteId) {

    }

}
