package me.shenchao.webhunger.client.processor;

import me.shenchao.webhunger.client.api.processor.AbstractHostHandler;
import me.shenchao.webhunger.entity.Host;

/**
 * 测试站点处理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class PrintHostHandler extends AbstractHostHandler {

    @Override
    protected void handleRequest(Host host) {
        System.out.println(host.getHostName() + "页面处理完毕");
    }
}
