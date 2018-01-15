package me.shenchao.webhunger.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理模块启动类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ProcessorBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    /**
     * 启动处理节点
     */
    public void start() {
        logger.info("处理节点正在启动......");
        // 解析配置
        parseProcessorConfig();
    }

    private void parseProcessorConfig() {

    }

    public static void main(String[] args) {
        new ProcessorBootstrap().start();
    }
}
