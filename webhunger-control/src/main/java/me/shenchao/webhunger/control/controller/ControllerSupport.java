package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.client.api.control.TaskAccessor;
import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.HostState;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.util.classloader.ThirdPartyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 控制器辅助类，主要用于数据访问操作
 *
 * @author Jerry Shen
 * @since 0.1
 */
class ControllerSupport {

    private static final Logger logger = LoggerFactory.getLogger(ControllerSupport.class);

    private TaskAccessor taskAccessor;

    ControllerSupport(ControlConfig controlConfig) {
        taskAccessor = getTaskAccessor(controlConfig);
    }

    List<Task> loadTasks() {
        return taskAccessor.loadTasks();
    }

    Task loadTaskById(String taskName) {
        return taskAccessor.loadTaskByName(taskName);
    }

    Host loadHostById(String hostId) {
        return taskAccessor.loadHostById(hostId);
    }

    void saveCrawlingSnapshot(HostCrawlingSnapshotDTO eventualCrawlingSnapshot) {
        taskAccessor.saveCrawlingSnapshot(eventualCrawlingSnapshot);
    }

    HostCrawlingSnapshotDTO encapsulateCrawlingSnapshot(Host host, HostCrawlingSnapshotDTO snapshot) {
        snapshot.setHostName(host.getHostName());
        snapshot.setHostIndex(host.getHostIndex());
        snapshot.setStartTime(host.getLatestSnapshot().getCreateTime());
        return snapshot;
    }

    /**
     * 加载用户自定义的Jar
     */
    private TaskAccessor getTaskAccessor(ControlConfig controlConfig) {
        String taskAccessorJarDir = controlConfig.getTaskAccessorJarDir();
        String taskAccessorClass = controlConfig.getTaskAccessorClass();

        TaskAccessor taskAccessor = ThirdPartyClassLoader.loadClass(taskAccessorJarDir, taskAccessorClass, TaskAccessor.class);
        if (taskAccessor == null) {
            logger.error("获取任务数据访问器失败，程序退出......");
            System.exit(1);
        }
        return taskAccessor;
    }

    void createSnapshot(Host host, HostState hostState) {
        HostSnapshot snapshot = new HostSnapshot(host, hostState.getState(), new Date());
        taskAccessor.createSnapshot(snapshot);
    }
}
