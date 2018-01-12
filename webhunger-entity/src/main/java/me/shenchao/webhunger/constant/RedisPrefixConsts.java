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
     *         value(list) -> urls
     *     </code>
     */
    private static final String QUEUE_PREFIX = "queue:";

    /**
     * 已爬列表前缀, 格式：<br>
     *     <code>
     *          key -> set:hostId
     *          value(set) -> urls
     *     </code>
     */
    private static final String SET_PREFIX = "set:";

    /**
     * 存放URL对应的具体内容，例如该URL属于第几层等信息，格式如下：
     *      <code>
     *          key -> item:hostId
     *          value(hash) ->
     *                         key1 -> url
     *                         value1(string) -> request
     *      </code>
     */
    private static final String ITEM_PREFIX = "item:";

    /**
     * 记录站点爬取过程中的统计信息，格式如下：
     *      <code>
     *          key -> count:hostId
     *          value(hash) ->
     *                        key1 -> success_page_num
     *                        value1(int) -> num
     *                        key2 -> error_page_num
     *                        value2(int) -> num
     *                        key3 -> error_pages
     *                        value3(list) -> error pages
     *      </code>
     *
     */
    private static final String COUNT_PREFIX = "count:";

    public static final String COUNT_SUCCESS_NUM = "success_page_num";

    public static final String COUNT_ERROR_NUM = "error_page_num";

    private static final String ERROR_PREFIX = "error:";

    public static String getQueueKey(String hostId) {
        return QUEUE_PREFIX + hostId;
    }

    public static String getSetKey(String hostId) {
        return SET_PREFIX + hostId;
    }

    public static String getItemKey(String hostId) {
        return ITEM_PREFIX + hostId;
    }

    public static String getCountKey(String hostId) {
        return COUNT_PREFIX + hostId;
    }

    public static String getErrorPrefix(String hostId) {
        return ERROR_PREFIX + hostId;
    }
}
