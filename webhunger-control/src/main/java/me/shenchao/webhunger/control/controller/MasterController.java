package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.control.scheduler.HostScheduler;
import me.shenchao.webhunger.control.scheduler.QueueHostScheduler;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostState;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.util.thread.CountableThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 中央控制器，决定爬虫任务的分发
 *
 * @author Jerry Shen
 * @since 0.1
 */
public abstract class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    private ControllerSupport controllerSupport;

    private ControlConfig controlConfig;

    /**
     * 重用WebMagic的线程池类，进行线程数量控制
     */
    private CountableThreadPool threadPool;

    private HostScheduler hostScheduler;

    /**
     * 等待新站点到来，需要先获取锁操作，配合newHostCondition一起使用
     */
    private Lock newHostLock = new ReentrantLock();

    private Condition newHostCondition = newHostLock.newCondition();

    /**
     * taskMap: 保存taskId 与 Task之间的映射关系<br>
     * 更新时机: <br>
     *     <ul>
     *         <li>
     *              只有在<b>任务页面</b>中点击刷新按钮时，会触发重新从数据源读取
     *         </li>
     *         <li>
     *              其余操作，例如读取站点列表、启动爬虫，停止等操作，只会从缓冲中读取，所以在程序运行过程中手动修改了数据源，
     *              请在任务显示页面刷新重新加载数据
     *         </li>
     *     </ul>
     */
    private Map<String, Task> taskMap;

    /**
     * hostMap: 保存hostId 与 Host之间的映射关系
     */
    private Map<String, Host> hostMap;

    MasterController(ControlConfig controlConfig) {
        this.controlConfig = controlConfig;
        controllerSupport = new ControllerSupport(controlConfig);
        // 启动站点调度器
        Thread schedulerThread = new Thread(new SchedulerThread());
        schedulerThread.start();
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
        if (taskMap == null)
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

    public void start(String hostId) {
        Host host = hostMap.get(hostId);
        logger.info("准备对站点：{} 爬取......", host.getHostName());
        // 清理数据，准备环境 todo
//        crawlersControlSupport.rollbackHost(host);
        // 修改host状态
        host.setState(HostState.Waiting.getState());
        // 生成host快照，记录当前状态情况
        controllerSupport.createSnapshot(host);
        hostScheduler.push(host);
        logger.info("站点：{} 加入待爬站点队列......", host.getHostName());
        signalNewHost();
    }

    /**
     * 向爬虫发送种子URL，开始爬取
     * @param host host
     */
    abstract void crawl(Host host);

    class SchedulerThread implements Runnable {

        @Override
        public void run() {
            initComponent();
            while (!Thread.currentThread().isInterrupted()) {
                Host host = hostScheduler.poll();
                if (host == null) {
                    waitNewHost();
                } else {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            logger.info("站点：{} 开始爬取......", host.getHostName());
                            host.setState(HostState.Crawling.getState());

                            crawl(host);
                        }
                    });
                }
            }
        }
    }

    private void waitNewHost() {
        newHostLock.lock();
        try {
            newHostCondition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            newHostLock.unlock();
        }
    }

    private void signalNewHost() {
        newHostLock.lock();
        try {
            newHostCondition.signalAll();
        } finally {
            newHostLock.unlock();
        }
    }

    /**
     * 初始化控制相关组件
     */
    private void initComponent() {
        String hostSchedulerStr = controlConfig.getHostSchedulerClass();
        try {
            Class<HostScheduler> hostSchedulerClass = (Class<HostScheduler>) Class.forName(hostSchedulerStr);
            hostScheduler = hostSchedulerClass.newInstance();
        } catch (Exception e) {
            logger.error("获取站点调度器失败，程序退出......", e);
            System.exit(1);
        }
        // 如果并行度设置小于0，表示同时爬取所有站点
        if (controlConfig.getParallelism() > 0)
            threadPool = new CountableThreadPool(controlConfig.getParallelism());
        else
            threadPool = new CountableThreadPool(Integer.MAX_VALUE);

        logger.info("站点调度器初始化完成，使用如下配置启动：");
        logger.info("Host Scheduler Name: {}", hostScheduler.getClass());
        logger.info("Host Crawling Parallelism: {}", controlConfig.getParallelism() > 0 ? controlConfig.getParallelism() : "all");
    }

}
