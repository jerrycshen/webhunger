package me.shenchao.webhunger.crawler.listener;

/**
 * 站点待爬URL数量监听器，访问该站点剩余待爬URL数量，以及总共数量
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface SiteUrlNumListener {

    /**
     * 获取该站点待爬URL数量
     * @param siteId siteId
     * @return 待爬URL剩余数量
     */
    int getLeftRequestsNum(String siteId);

    /**
     * 获取该站点所有URL数量
     * @param siteId siteId
     * @return 所有url数量
     */
    int getTotalRequestsNum(String siteId);
}
