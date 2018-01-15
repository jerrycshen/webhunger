package me.shenchao.webhunger.processor.selector;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.listener.HostPageNumListener;

/**
 * 页面处理模块站点选择器，选择哪个站点页面去处理
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface HostSelector {

    /**
     * 获取下一个页面处理的站点
     * @param hostListener listener
     * @return next host to
     */
    Host select(HostPageNumListener hostListener);
}
