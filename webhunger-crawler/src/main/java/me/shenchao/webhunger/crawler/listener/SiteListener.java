package me.shenchao.webhunger.crawler.listener;

public interface SiteListener {

    int getLeftRequestsCount(String siteId);

    int getTotalRequestsCount(String siteId);
}
