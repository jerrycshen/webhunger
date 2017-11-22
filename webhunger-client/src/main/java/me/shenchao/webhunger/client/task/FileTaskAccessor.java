package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.client.api.TaskAccessor;
import me.shenchao.webhunger.config.WebHungerConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.exception.TaskParseException;
import me.shenchao.webhunger.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * 从文件中读取任务信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class FileTaskAccessor implements TaskAccessor {

    private static final Logger logger = LoggerFactory.getLogger(FileTaskAccessor.class);

    private static final String DEFAULT_TASK_PATH = SystemUtil.getWebHungerDefaultDir() + File.separator + "task";

    private WebHungerConfig webHungerConfig;

    private FileParser taskParser = new FileParser();

    private FileAccessSupport fileSupport = new FileAccessSupport(getTaskDataDir());

    @Override
    public List<Task> loadTasks(WebHungerConfig webHungerConfig) {
        this.webHungerConfig = webHungerConfig;

        List<Task> tasks = new ArrayList<>();
        // 1. 获取task配置文件
        File[] taskFiles = fileSupport.getTaskFiles();
        logger.info("共找到{}个task文件", taskFiles.length);

        // 2. 解析task配置文件
        for (File taskFile : taskFiles) {
            try {
                logger.info("解析{}......", taskFile.getName());
                tasks.add(taskParser.parseTask(taskFile));
            } catch (TaskParseException e) {
                logger.error("解析{}失败，请检查文件格式......{}", taskFile.getAbsoluteFile(), e);
                e.printStackTrace();
            }
        }
        return tasks;
    }

    @Override
    public void createSnapshot(HostSnapshot snapshot) {

    }

    @Override
    public void saveErrorPages(Host host) {

    }

    private String getTaskDataDir() {
        return webHungerConfig.getConfMap().getOrDefault("taskDataDir", DEFAULT_TASK_PATH);
    }


}
