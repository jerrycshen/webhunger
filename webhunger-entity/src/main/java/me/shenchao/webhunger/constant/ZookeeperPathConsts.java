package me.shenchao.webhunger.constant;

import me.shenchao.webhunger.util.common.SystemUtils;

/**
 * 记录zookeeper中节点路径的常量类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ZookeeperPathConsts {

    public static final String APP_PREFIX = "/webhunger";

    /**
     * 记录所有爬虫节点
     */
    public static final String CRAWLER = APP_PREFIX + "/crawler";

    /**
     * 记录所有在爬站点的详细信息，站点信息使用json保存
     */
    public static final String DETAIL_HOST = APP_PREFIX + "/detail_host";

    /**
     * 记录的节点值表示当前有多少个爬虫站点已经对站点完成了爬取工作，用于控制器判断站点是否爬取完毕
     */
    public static final String CRAWLING_HOST = APP_PREFIX + "/crawling_host";

    public static final String PROCESSING_HOST = APP_PREFIX + "/processing_host";

    public static final String LOCK = APP_PREFIX + "/lock";

    public static String getCrawlerNodePath() {
        return CRAWLER + "/" + SystemUtils.getHostName();
    }

}
