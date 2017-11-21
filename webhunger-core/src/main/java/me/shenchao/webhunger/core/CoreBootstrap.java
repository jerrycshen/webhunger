package me.shenchao.webhunger.core;

import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.util.SystemUtil;
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
public class CoreBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CoreBootstrap.class);

    private static final String CONF_NAME = "webhunger.conf";

    private WebHungerConfig webHungerConfig;

    private void parseCoreConfig() {
        webHungerConfig = new WebHungerConfig();
        try {
            webHungerConfig.parse(SystemUtil.getWebHungerConfigDir() + File.separator + CONF_NAME);
        } catch (Exception e) {
            logger.error("配置文件解析失败，程序退出......", e);
            System.exit(1);
        }
    }

    private void start() {
        // 读取任务数据
        

        // 启动web控制台
        try {
            new WebConsoleStarter().startServer(webHungerConfig);
        } catch (Exception e) {
            logger.error("Web控制台启动失败，程序退出......", e);
            System.exit(1);
        }
    }




    public static void main(String[] args) {
        CoreBootstrap bootstrap = new CoreBootstrap();
        bootstrap.parseCoreConfig();
        bootstrap.start();
    }
}
