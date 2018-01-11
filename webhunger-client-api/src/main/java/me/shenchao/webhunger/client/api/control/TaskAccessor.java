package me.shenchao.webhunger.client.api.control;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.Task;

import java.util.List;

/**
 * 爬虫任务数据访问器。<Br>
 *
 * @author Jerry Shen
 * @since 0.1
 */
public interface TaskAccessor {

    /**
     * 从指定数据源加载任务数据，这里只加载task的具体信息，不会加载host
     *
     * @return all tasks
     */
    List<Task> loadTasks();

    /**
     * 获取指定任务及其所包含的所有站点
     *
     *  @param taskName 指定任务
     * @return task
     */
    Task loadTaskByName(String taskName);

    /**
     * 获取指定Host
     * @param hostId hostId
     * @return host
     */
    Host loadHostById(String hostId);

    /**
     * 为站点状态创建快照，记录重要时间节点，
     * @param snapshot snapshot
     */
    void createSnapshot(HostSnapshot snapshot);

    /**
     * 爬取完毕后，保存爬取过程中错误的页面
     * @param host host
     */
    void saveErrorPages(Host host);
}
