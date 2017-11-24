package me.shenchao.webhunger.controller;

import me.shenchao.webhunger.config.WebHungerConfig;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class StandaloneController extends MasterController {

    StandaloneController(WebHungerConfig webHungerConfig) {
        super(webHungerConfig);
    }
}
