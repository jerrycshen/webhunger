package me.shenchao.webhunger.crawler.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Set;

/**
 * @author Jerry Shen
 * @since 0.1
 */
public class URLDiscovery implements PageProcessor {

    private Host host;

    private UrlFilterChain filterChain;

    public URLDiscovery(Host host) {
        this.host = host;
        filterChain = UrlFilterChainFactory.getWebUrlFilterChain(host);
    }

    @Override
    public void process(Page page) {
        PageInfo pageInfo = transferToPageInfo(page);

        // 得到待爬URL集合
        Set<String> newUrls = ExtractNewUrlsHelper.extractAllUrls(pageInfo);
        filterChain.doFilter(pageInfo, newUrls);

        // 加入待爬列表
        for (String readyCrawl : newUrls) {
            page.addTargetRequest(readyCrawl);
        }

        page.putField("pageInfo", pageInfo);
    }

    @Override
    public Site getSite() {
        return Site.me().setHost(host).setSleepTime(host.getBasic_config().getPoliteness_delay())
                .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36")
                .setTimeOut(30000)
                .setCycleRetryTimes(2)
                .addHeader(HttpConstant.Header.REFERER, host.getHost_index());
    }

    private PageInfo transferToPageInfo(Page page) {
        PageInfo pageInfo = new PageInfo();

        pageInfo.setHost(host);
        pageInfo.setUrl(page.getRequest().getUrl());
        pageInfo.setDepth(page.getRequest().getNowDepth());
        pageInfo.setRawText(page.getRawText());
        pageInfo.setResponseHeader(page.getHeaders());
        pageInfo.setBytes(page.getBytes());
        pageInfo.setCharset(page.getCharset());
        pageInfo.setParentUrl(page.getRequest().getParentUrl());

        return pageInfo;
    }

}
