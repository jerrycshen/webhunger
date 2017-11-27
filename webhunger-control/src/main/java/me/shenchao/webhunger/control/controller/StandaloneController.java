package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class StandaloneController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneController.class);

    StandaloneController(ControlConfig controlConfig) {
        super(controlConfig);
    }

}
