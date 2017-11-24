package me.shenchao.webhunger.core.controller;

import me.shenchao.webhunger.client.api.TaskAccessor;
import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 中央控制器，决定爬虫任务的分发
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    private ControllerSupport controllerSupport;

    MasterController(WebHungerConfig webHungerConfig) {
        controllerSupport = new ControllerSupport(webHungerConfig);
    }

    public List<Task> getTasks() {
        Map<String, Task> taskMap = controllerSupport.getTasks();
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<String, Task> entry : taskMap.entrySet()) {
            tasks.add(entry.getValue());
        }
        return tasks;
    }

    public List<Host> getHostsByTaskId(String taskId) {
        Map<String, Task> taskMap = controllerSupport.getTasks();
        return taskMap.get(taskId).getHosts();
    }

}
