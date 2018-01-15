package me.shenchao.webhunger.processor.listener;

/**
 * 站点待处理Page数量监听器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface HostPageNumListener {

    /**
     * 获取该站点待处理页面数量
     * @param hostId hostId
     * @return 待处理页面数量
     */
    int getLeftPagesNum(String hostId);

}
