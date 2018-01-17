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

    /**
     * 清楚站点之前爬取处理过程中留下的足迹，回滚到最初状态，每次爬取/重爬取都会调用该方法<br>
     *
     *     现阶段，一旦调用回滚操作，需要进行如下操作<Br>
     *     <ul>
     *         <li>删除站点所有的快照信息</li>
     *         <li>删除站点保存的所有错误页面</li>
     *         <LI>删除站点保存的结果信息</LI>
     *         <li>其他留下的足迹，例如存储的页面处理信息等; 根据需要进行回滚</li>
     *     </ul>
     *
     * @param hostId hostId
     */
    void rollbackHost(String hostId);
}
