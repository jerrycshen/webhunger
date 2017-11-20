package me.shenchao.webhunger.core;

import me.shenchao.webhunger.core.config.CoreConfig;
import me.shenchao.webhunger.core.util.SystemUtil;
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

    private CoreConfig coreConfig;

    public void parseCoreConfig() {
        coreConfig = new CoreConfig();
        try {
            coreConfig.parse(SystemUtil.getWebHungerConfig() + File.separator + CONF_NAME);
        } catch (Exception e) {
            logger.error("配置文件解析失败，程序退出......", e);
            System.exit(1);
        }
    }

    public void start() {
        try {
            new WebConsoleStarter().startServer(coreConfig.getPort(), coreConfig.getContextPath());
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
