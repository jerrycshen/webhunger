package me.shenchao.webhunger.crawler.caller;

import com.google.common.collect.Lists;
import me.shenchao.webhunger.crawler.dominate.LocalSiteDominate;
import me.shenchao.webhunger.crawler.listener.BaseSpiderListener;
import me.shenchao.webhunger.crawler.util.HostSnapshotHelper;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.rpc.api.crawler.CrawlerCallable;

import java.util.List;

/**
 * 单机版爬虫控制器，接受本地方法调用控制爬虫操作
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class LocalCrawlerCaller implements CrawlerCallable {

    private LocalSiteDominate siteDominate;

    private BaseSpiderListener spiderListener;

    public LocalCrawlerCaller(LocalSiteDominate siteDominate, BaseSpiderListener spiderListener) {
        this.siteDominate = siteDominate;
        this.spiderListener = spiderListener;
    }

    /**
     * 当为单机版爬虫时，爬虫已启动就处于Running状态，所以无须调用该方法
     */
    @Override
    public void run() {}

    @Override
    public void crawl(List<Host> hosts) {
        List<Site> newSiteList = Lists.newArrayList();
        for (Host host : hosts) {
            Site site = new Site(host);
            newSiteList.add(site);
        }
        siteDominate.addSeedUrls(newSiteList);
        siteDominate.updateLocalCrawlingSiteList(newSiteList);
    }

    @Override
    public HostCrawlingSnapshotDTO createSnapshot(String hostId) {
        return HostSnapshotHelper.create(hostId, spiderListener.getSiteStatusStatistics(hostId));
    }


}
