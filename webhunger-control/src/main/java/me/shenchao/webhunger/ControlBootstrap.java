package me.shenchao.webhunger;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.control.controller.ControllerFactory;
import me.shenchao.webhunger.exception.ConfigParseException;
import me.shenchao.webhunger.util.common.SystemUtil;
import me.shenchao.webhunger.web.WebConsoleStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Core Control module bootstrap
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ControlBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ControlBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private ControlConfig controlConfig;

    private void parseControlConfig() {
        controlConfig = new ControlConfig();
        try {
            controlConfig.parse(SystemUtil.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (ConfigParseException e) {
            logger.warn(e.toString());
        } catch (Exception e) {
            logger.error("配置文件解析失败，程序退出......", e);
            System.exit(1);
        }
        // log config info
        logger.info("配置解析完成，使用如下参数启动程序：");
        logger.info("Distributed: {}", controlConfig.isDistributed());
        logger.info("Task Accessor Jar Dir: {}", controlConfig.getTaskAccessorJarDir());
        logger.info("Task Accessor Class: {}", controlConfig.getTaskAccessorClass());
        logger.info("Jetty Port: {}", controlConfig.getPort());
        logger.info("Web Context Path: {}", controlConfig.getContentPath());
    }

    private void start() {
        // 初始化中央控制器
        ControllerFactory.initController(controlConfig);

        // 启动web控制台
        try {
            new WebConsoleStarter().startServer(controlConfig.getPort(), controlConfig.getContentPath());
        } catch (Exception e) {
            logger.error("Web控制台启动失败，程序退出......", e);
            System.exit(1);
        }
        logger.info("Web控制台启动完成......");
        logger.info("WebHunger WebConsole available at http://localhost:5572/webhunger/task/list");
    }

    public static void main(String[] args) {
        ControlBootstrap bootstrap = new ControlBootstrap();
        bootstrap.parseControlConfig();
        bootstrap.start();
    }
}
