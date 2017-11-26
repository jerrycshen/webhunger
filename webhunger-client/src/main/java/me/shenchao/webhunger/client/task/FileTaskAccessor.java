package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.client.api.TaskAccessor;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.exception.TaskParseException;
import me.shenchao.webhunger.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 从文件中读取任务信息
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class FileTaskAccessor implements TaskAccessor {

    private static final Logger logger = LoggerFactory.getLogger(FileTaskAccessor.class);

    private static final String DEFAULT_TASK_PATH = SystemUtil.getWebHungerDefaultDir() + File.separator + "tasks";

    private static final String SNAPSHOT_NAME = "host.snapshot";

    @Override
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        // 1. 获取task配置文件
        File[] taskFiles = FileAccessSupport.getTaskFiles(DEFAULT_TASK_PATH);
        logger.info("共找到{}个task文件", taskFiles.length);

        // 2. 解析task配置文件
        for (File taskFile : taskFiles) {
            try {
                logger.info("解析{}......", taskFile.getName());
                Task task = FileParser.parseTask(taskFile);
                tasks.add(task);
            } catch (TaskParseException e) {
                logger.error("解析{}失败，请检查文件格式......{}", taskFile.getAbsoluteFile(), e);
            }
        }

        // 3. 从快照日志中恢复站点状态
        for (Task task : tasks) {
            List<Host> hosts = task.getHosts();
            for (Host host : hosts) {
                HostSnapshot hostSnapshot = null;
                try {
                    hostSnapshot = FileAccessSupport.getLatestSnapshot(getSnapshotPath(host));
                } catch (IOException e) {
                    logger.error("读取：{} 快照文件失败......{}", getSnapshotPath(host), e);
                }
                if (hostSnapshot != null) {
                    host.setState(hostSnapshot.getState());
                } else {
                    host.setState(0);
                }
            }
        }

        return tasks;
    }

    @Override
    public void createSnapshot(HostSnapshot snapshot) {
        try {
            FileAccessSupport.addSnapshot(getSnapshotPath(snapshot.getHost()), snapshot);
        } catch (IOException e) {
            logger.error("写入：{} 快照文件失败......{}", getSnapshotPath(snapshot.getHost()), e);
        }
    }

    @Override
    public void saveErrorPages(Host host) {

    }

    private String getSnapshotPath(Host host) {
        return getHostDir(host) + File.separator + SNAPSHOT_NAME;
    }

    private String getHostDir(Host host) {
        return DEFAULT_TASK_PATH + File.separator + "result" + File.separator + host.getTask().getTaskName() +
                File.separator + getHostFolderName(host);
    }

    /**
     * 根据task name 与 站点域名 生成站点文件夹名称
     * @return taskName-hostDomain.index
     */
    private String getHostFolderName(Host host) {
        return host.getTask().getTaskName() + "-" + host.getHostDomain();
    }

}
