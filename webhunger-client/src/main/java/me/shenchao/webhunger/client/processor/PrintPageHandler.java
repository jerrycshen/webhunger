package me.shenchao.webhunger.client.processor;

import me.shenchao.webhunger.client.api.processor.AbstractPageHandler;
import me.shenchao.webhunger.dto.PageDTO;

/**
 * 测试页面处理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class PrintPageHandler extends AbstractPageHandler {

    @Override
    public void handRequest(PageDTO page) {
        System.out.println(page.getUrl());
    }
}
