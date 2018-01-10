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
        assert masterController != null;
        return masterController;
    }

    public static void initController(ControlConfig controlConfig) {
        if (masterController == null) {
            if (!controlConfig.isDistributed()) {
                masterController = new LocalController(controlConfig);
                logger.info("单机版控制器初始化完毕......");
            } else {
                masterController = new DistributedController(controlConfig);
                logger.info("分布式控制器初始化完毕......");
            }
        } else {
            logger.warn("中央控制器正在运行中，请不要重复初始化......");
        }
    }
}
