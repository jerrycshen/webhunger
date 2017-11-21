package me.shenchao.webhunger.client;

import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Task;

import java.util.List;

/**
 * 爬虫任务数据加载器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface TaskLoader {

    List<Task> loadTasks(WebHungerConfig webHungerConfig);
}
