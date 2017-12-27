package me.shenchao.webhunger.crawler.processor;

import me.shenchao.webhunger.client.api.crawler.URLFilterChain;
import me.shenchao.webhunger.crawler.filter.UrlFilterChainFactory;
import me.shenchao.webhunger.crawler.util.ExtractNewUrlsHelper;
import me.shenchao.webhunger.dto.PageDTO;
import me.shenchao.webhunger.entity.webmagic.Site;
import us.codecraft.webmagic.LifeCycle;
import me.shenchao.webhunger.entity.webmagic.Page;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Set;

/**
 * 用于爬取整个站点的处理器
 *
 * 两个作用：<br>
 *    1. 提取新的URL，并交给URL过滤链处理，最后加入待爬列表
 *    2. 构造用于最后处理的PageInfo对象
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class WholeSiteCrawledProcessor implements PageProcessor {

    @Override
    public void process(Page page, LifeCycle lifeCycle) {
        // 提取该页面中的所有URL集合
        Set<String> newUrls = ExtractNewUrlsHelper.extractAllUrls(page);
        // 获取过滤链
        Site site = getSite(page, lifeCycle);
        URLFilterChain urlFilterChain = UrlFilterChainFactory.getURLFilterChain(site);
        urlFilterChain.doFilter(page, site, newUrls);

        // 过滤后的URL 加入待爬列表
        for (String newUrl : newUrls) {
            page.addTargetRequest(newUrl);
        }

        PageDTO pageDTO = copyPageInfo(page);
        page.putField("page", pageDTO);
    }

    private Site getSite(Page page, LifeCycle lifeCycle) {
        return lifeCycle.getSites().get(page.getRequest().getSiteId());
    }

    private PageDTO copyPageInfo(Page page) {
        PageDTO pageDTO = new PageDTO();

        pageDTO.setSiteId(page.getRequest().getSiteId());
        pageDTO.setUrl(page.getRequest().getUrl());
        pageDTO.setParentUrl(page.getRequest().getParentUrl());
        pageDTO.setDepth(page.getRequest().getNowDepth());
        pageDTO.setRawText(page.getRawText());
        pageDTO.setCharset(page.getCharset());
        return pageDTO;
    }
}
