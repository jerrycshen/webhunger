package us.codecraft.webmagic;

import java.util.List;

/**
 *  爬虫生命周期接口
 */
public interface LifeCycle {

    /**
     * unique id for a spider.
     *
     * @return uuid
     */
    public String getUUID();

    /**
     * 正在爬取的所有站点集合
     *
     * @return sites
     */
    public Site getSites();

}
