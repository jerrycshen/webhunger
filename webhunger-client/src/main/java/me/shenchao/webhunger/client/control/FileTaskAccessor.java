package me.shenchao.webhunger.client.control;

import me.shenchao.webhunger.client.api.control.TaskAccessor;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.util.common.SystemUtils;
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

    private static final String DEFAULT_TASK_PATH = SystemUtils.getWebHungerDefaultDir() + File.separator + "tasks";

    private static final String DEFAULT_RESULT_PATH = SystemUtils.getWebHungerDefaultDir() + File.separator + "result";

    private static final String SNAPSHOT_NAME = "host.snapshot";

    @Override
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        // 1. 获取task配置文件
        File[] taskFiles = FileAccessSupport.getTaskFiles(DEFAULT_TASK_PATH);

        // 2. 解析task配置文件
        for (File taskFile : taskFiles) {
            Task task = FileParser.parseTask(taskFile, false);
            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public Task loadTaskByName(String taskName) {
        File taskFile = FileAccessSupport.getTaskFile(DEFAULT_TASK_PATH, taskName);
        if (taskFile == null) {
            throw new RuntimeException("找不到指定任务文件: " + taskFile.getAbsolutePath());
        }
        Task task = FileParser.parseTask(taskFile, true);
        List<Host> hosts = task.getHosts();
        // 从快照日志中恢复站点状态
        for (Host host : hosts) {
            recoveryHostStat(host);
        }
        return task;
    }

    @Override
    public Host loadHostById(String hostId) {
        File[] taskFiles = FileAccessSupport.getTaskFiles(DEFAULT_TASK_PATH);
        for (File file : taskFiles) {
            Task task = FileParser.parseTask(file, true);
            for (Host host : task.getHosts()) {
                if (host.getHostId().equals(hostId)) {
                    recoveryHostStat(host);
                    return host;
                }
            }
        }
        throw new RuntimeException("未找到指定站点......");
    }

    @Override
    public void createSnapshot(HostSnapshot snapshot) {
        try {
            FileAccessSupport.createSnapshot(getSnapshotPath(snapshot.getHost()), snapshot);
        } catch (IOException e) {
            logger.error("写入：{} 快照文件失败......", getSnapshotPath(snapshot.getHost()), e);
        }
    }

    @Override
    public void saveCrawlingSnapshot(HostCrawlingSnapshotDTO snapshot) {

    }

    /**
     * 根据站点的快照信息，设置其最新状态信息
     * @param host host
     */
    private void recoveryHostStat(Host host) {
        HostSnapshot hostSnapshot = null;
        try {
            hostSnapshot = FileAccessSupport.getLatestSnapshot(host, getSnapshotPath(host));
        } catch (IOException e) {
            logger.error("读取：{} 快照文件失败......{}", getSnapshotPath(host), e);
        }
        if (hostSnapshot != null) {
            host.setLatestSnapshot(hostSnapshot);
        } else {
            HostSnapshot emptySnapshot = new HostSnapshot(host, 0, new Date());
            host.setLatestSnapshot(emptySnapshot);
        }
    }

    private String getSnapshotPath(Host host) {
        return getHostDir(host) + File.separator + SNAPSHOT_NAME;
    }

    private String getHostDir(Host host) {
        return DEFAULT_RESULT_PATH + File.separator + host.getTask().getTaskName() +
                File.separator + host.getHostDomain();
    }

}
