package me.shenchao.webhunger.client.api.control;

import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.entity.*;

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
     * 爬取完毕后，保存爬取结果
     * @param crawledResult crawledResult
     * @param errorPages error pages
     */
    void saveCrawledResult(CrawledResult crawledResult, List<ErrorPageDTO> errorPages);

    /**
     * 获取站点爬取处理结果
     * @param hostId hostId
     * @return host result
     */
    HostResult getHostResult(String hostId);

    /**
     * 获取分页错误页面
     *
     * @param hostId hostId
     * @param startPos start position
     * @param size size
     * @return 分页的错误页面
     */
    List<ErrorPageDTO> getErrorPages(String hostId, int startPos, int size);

    /**
     * 获得错误页面数量
     * @param hostId hostId
     * @return the error page num
     */
    int getErrorPageNum(String hostId);
}
