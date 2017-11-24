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

    private static final String DEFAULT_TASK_PATH = SystemUtil.getWebHungerDefaultDir() + File.separator + "sample";

    private WebHungerConfig webHungerConfig;

    @Override
    public Map<String, Task> loadTasks(WebHungerConfig webHungerConfig) {
        this.webHungerConfig = webHungerConfig;

        Map<String, Task> taskMap = new HashMap<>();
        // 1. 获取task配置文件
        File[] taskFiles = FileAccessSupport.getTaskFiles(getTaskDataDir());
        logger.info("共找到{}个task文件", taskFiles.length);

        // 2. 解析task配置文件
        for (File taskFile : taskFiles) {
            try {
                logger.info("解析{}......", taskFile.getName());
                Task task = FileParser.parseTask(taskFile);
                taskMap.put(task.getTaskId(), task);
            } catch (TaskParseException e) {
                logger.error("解析{}失败，请检查文件格式......{}", taskFile.getAbsoluteFile(), e);
                e.printStackTrace();
            }
        }

        // 3. 从快照日志中恢复站点状态
        for (Map.Entry<String, Task> entry : taskMap.entrySet()) {
            Task task = entry.getValue();
            List<Host> hosts = task.getHosts();
            for (Host host : hosts) {
                HostSnapshot hostSnapshot = FileAccessSupport.getLatestSnapshot(getHostResultDir(host));
                if (hostSnapshot != null) {
                    host.setState(hostSnapshot.getState());
                } else {
                    host.setState(0);
                }
            }
        }

        return taskMap;
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


    private String getHostResultDir(Host host) {
        return getTaskDataDir() + File.separator + "result" + File.separator + host.getTask().getTaskName() +
                File.separator + FileAccessSupport.getHostFolderName(host);
    }

}
