package me.shenchao.webhunger.constant;

/**
 * redis相关的前缀常量
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RedisPrefixConsts {

    /**
     * 待爬队列前缀, 格式如下：<br>
     *     <code>
     *         key -> queue:hostId
     *         value -> url
     *     </code>
     */
    public static final String QUEUE_PREFIX = "queue:";

    /**
     * 已爬列表前缀, 格式：<br>
     *     <code>
     *          key -> set:hostId
     *          value -> url
     *     </code>
     */
    public static final String SET_PREFIX = "set:";

    /**
     * 存放URL对应的具体内容，例如该URL属于第几层等信息，格式如下：
     *      <code>
     *          key -> item:hostId
     *          value -> key -> url
     *                   value -> request
     *      </code>
     */
    public static final String ITEM_PREFIX = "item:";

    public static String getQueueKey(String hostId) {
        return QUEUE_PREFIX + hostId;
    }

    public static String getSetKey(String hostId) {
        return SET_PREFIX + hostId;
    }

    public static String getItemKey(String hostId) {
        return ITEM_PREFIX + hostId;
    }
}
