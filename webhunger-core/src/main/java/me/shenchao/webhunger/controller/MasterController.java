package me.shenchao.webhunger.controller;

import me.shenchao.webhunger.client.api.TaskAccessor;
import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 中央控制器，决定爬虫任务的分发
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    private TaskAccessor taskAccessor;

    protected WebHungerConfig webHungerConfig;

    private ControllerSupport controllerSupport = new ControllerSupport();

    MasterController(WebHungerConfig webHungerConfig) {
        this.webHungerConfig = webHungerConfig;
        taskAccessor = controllerSupport.getTaskLoader(webHungerConfig);
    }

    public List<Task> getTasks() {
        return taskAccessor.loadTasks(webHungerConfig);
    }

}
