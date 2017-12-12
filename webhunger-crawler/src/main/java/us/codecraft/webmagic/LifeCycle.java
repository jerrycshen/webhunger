package us.codecraft.webmagic;

import me.shenchao.webhunger.entity.webmagic.Site;

import java.util.Map;

/**
 *  爬虫生命周期接口
 */
public interface LifeCycle {

    /**
     * unique id for a spider.
     *
     * @return uuid
     */
    String getUUID();

    /**
     * 正在爬取的所有站点集合
     *
     * @return sites
     */
    Map<String, Site> getSites();

}
