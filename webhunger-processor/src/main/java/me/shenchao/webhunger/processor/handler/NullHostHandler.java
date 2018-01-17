package me.shenchao.webhunger.processor.handler;

import me.shenchao.webhunger.client.api.processor.AbstractHostHandler;
import me.shenchao.webhunger.entity.Host;

/**
 * 空页面处理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class NullHostHandler extends AbstractHostHandler{

    @Override
    protected void handleRequest(Host host) {

    }
}
