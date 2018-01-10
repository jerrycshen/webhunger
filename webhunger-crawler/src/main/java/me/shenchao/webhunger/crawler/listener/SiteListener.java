package me.shenchao.webhunger.crawler.listener;

/**
 * 站点监听器，访问该站点剩余待爬URL数量，以及总共数量
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface SiteListener {

    int getLeftRequestsCount(String siteId);

    int getTotalRequestsCount(String siteId);
}
