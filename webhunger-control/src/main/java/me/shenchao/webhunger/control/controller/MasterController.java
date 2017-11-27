package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中央控制器，决定爬虫任务的分发
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    ControllerSupport controllerSupport;

    /**
     * taskMap: 保存taskId 与 Task之间的映射关系<br>
     * 更新时机: <br>
     *     <ul>
     *         <li>
     *              只有在页面中点击刷新按钮时，会触发重新从数据源读取
     *         </li>
     *         <li>
     *              其余操作，例如启动爬虫，停止等操作，只会从缓冲中读取，所以在程序运行过程中手动修改了数据源，请通过刷新按钮重新加载数据，保证数据一致性
     *         </li>
     *     </ul>
     */
    Map<String, Task> taskMap;

    /**
     * hostMap: 保存hostId 与 Host之间的映射关系
     */
    Map<String, Host> hostMap;

    MasterController(ControlConfig controlConfig) {
        controllerSupport = new ControllerSupport(controlConfig);
    }

    public List<Task> getTasks() {
        loadTasks();
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<String, Task> entry : taskMap.entrySet()) {
            tasks.add(entry.getValue());
        }
        return tasks;
    }

    public List<Host> getHostsByTaskId(String taskId) {
        loadTasks();
        return taskMap.get(taskId).getHosts();
    }

    public Task getTaskById(String taskId) {
        if (taskMap == null)
            loadTasks();
        return taskMap.get(taskId);
    }

    public Host getHostById(String hostId) {
        return hostMap.get(hostId);
    }

    public abstract void start(String hostId);

    /**
     * TODO 存在并发问题，如果在加载过程中，读取hostMap中的数据
     */
    private void loadTasks() {
        List<Task> tasks = controllerSupport.loadTasks();
        taskMap = new HashMap<>();
        hostMap = new HashMap<>();
        for (Task task : tasks) {
            List<Host> hosts = task.getHosts();
            taskMap.put(task.getTaskId(), task);
            for (Host host : hosts) {
                hostMap.put(host.getHostId(), host);
            }
        }
    }

}
