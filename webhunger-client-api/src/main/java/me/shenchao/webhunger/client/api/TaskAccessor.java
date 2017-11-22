package me.shenchao.webhunger.client.api;

import me.shenchao.webhunger.config.WebHungerConfig;
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
     * 从指定数据源加载任务数据
     *
     * <note>由于快照记录有站点的最新状态信息，自定义实现时需要将最新的状态读取出来</note>
     * @param webHungerConfig 配置参数
     */
    List<Task> loadTasks(WebHungerConfig webHungerConfig);

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
