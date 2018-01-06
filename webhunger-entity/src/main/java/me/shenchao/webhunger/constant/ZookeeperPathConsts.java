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

    public static final String CRAWLER = APP_PREFIX + "/crawler";

    public static String getCrawlerPath() {
        return CRAWLER + "/" + SystemUtils.getHostName();
    }

}
