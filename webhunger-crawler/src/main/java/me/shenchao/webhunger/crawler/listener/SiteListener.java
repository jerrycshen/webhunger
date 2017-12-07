package me.shenchao.webhunger.crawler.listener;

import us.codecraft.webmagic.LifeCycle;

public interface SiteListener {

    int getLeftRequestsCount(LifeCycle lifeCycle, String siteId);

    int getTotalRequestsCount(LifeCycle lifeCycle, String siteId);
}
