package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据配置决定采用单机还是分布式控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ControllerFactory {

    private static final Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    private static volatile MasterController masterController = null;

    private ControllerFactory() {}

    public static MasterController getController() {
        if (masterController == null) {
            logger.error("中央控制器尚未初始化，程序退出......");
            System.exit(1);
        }
        return masterController;
    }

    public static void initController(ControlConfig controlConfig) {
        if (masterController == null) {
            synchronized (ControllerFactory.class) {
                if (masterController == null) {
                    if (!controlConfig.isDistributed()) {
                        masterController = new StandaloneController(controlConfig);
                        logger.info("单机式控制器初始化完毕......");
                    } else {
                        logger.info("分布式控制器初始化完毕......");
                    }
                }
            }
        } else {
            logger.warn("中央控制器正在运行中，请不要重复初始化......");
        }
    }
}