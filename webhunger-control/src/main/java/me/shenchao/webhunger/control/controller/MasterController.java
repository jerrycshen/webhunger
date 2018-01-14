package me.shenchao.webhunger.control.controller;

import com.google.common.collect.Maps;
import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.control.scheduler.HostScheduler;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.dto.HostCrawlingSnapshotDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostState;
import me.shenchao.webhunger.entity.Task;
import me.shenchao.webhunger.util.thread.CountableThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected ControllerSupport controllerSupport;

    protected ControlConfig controlConfig;

    private boolean distributed;

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
     * 当前正在爬取或者页面处理的站点集合
     */
    protected Map<String, Host> crawlingHostMap = Maps.newHashMap();

    protected ReentrantLock lock = new ReentrantLock();

    MasterController(ControlConfig controlConfig, boolean distributed) {
        this.controlConfig = controlConfig;
        this.distributed = distributed;
        controllerSupport = new ControllerSupport(controlConfig);
        // 启动站点调度器
        Thread schedulerThread = new Thread(new SchedulerThread());
        schedulerThread.start();
    }

    /**
     * 获得所有任务信息
     * @return all tasks
     */
    public List<Task> getTasks() {
        return controllerSupport.loadTasks();
    }

    /**
     * 根据任务名称获得指定任务
     * @param taskName taskName
     * @return the task whose name is taskName
     */
    public Task getTaskByName(String taskName) {
        return controllerSupport.loadTaskById(taskName);
    }

    /**
     * 获得指定ID的站点
     * @param hostId hostId
     * @return the host whose id is hostId
     */
    public Host getHostById(String hostId) {
        return controllerSupport.loadHostById(hostId);
    }

    /**
     * 启动对该站点爬取
     *
     * @param hostId hostId
     */
    public synchronized void start(String hostId) {
        Host host = controllerSupport.loadHostById(hostId);
        // 检查是否已经开始爬取
        if (host.getLatestSnapshot().getState() != HostState.Ready.getState()) {
            logger.warn("站点：{} 已经开始爬取......", host.getHostName());
            return;
        }

        logger.info("准备对站点：{} 爬取......", host.getHostName());
        // 清理数据，准备环境 todo
//        crawlersControlSupport.rollbackHost(host);
        // 生成host快照，记录当前状态情况
        controllerSupport.createSnapshot(host, HostState.Waiting);
        hostScheduler.push(host);
        logger.info("站点：{} 加入待爬站点队列......", host.getHostName());
        signalNewHost();
    }

    /**
     * 当前爬取环境是否为分布式
     * @return return true if is distributed mode
     */
    public boolean isDistributed() {
        return distributed;
    }

    /**
     * 分页获取错误请求页面
     *
     * @param hostId hostId
     * @param startPos start index
     * @param size list size
     * @return partition error pages
     */
    public abstract List<ErrorPageDTO> getErrorPages(String hostId, int startPos, int size);

    /**
     * 获得错误页面的总数量
     * @param hostId hostId
     * @return the total num of error pages
     */
    public abstract int getErrorPageNum(String hostId);

    /**
     * 获取站点当前快照
     * @param hostId hostId
     * @return the crawling snapshot of host
     */
    public HostCrawlingSnapshotDTO getCurrentCrawlingSnapshot(String hostId) {
        Host crawlingHost;
        if ((crawlingHost = crawlingHostMap.get(hostId)) == null) {
            return null;
        }
        HostCrawlingSnapshotDTO snapshot = createCrawlingSnapshot(hostId);
        snapshot.setHostName(crawlingHost.getHostName());
        snapshot.setHostIndex(crawlingHost.getHostIndex());
        snapshot.setStartTime(crawlingHost.getLatestSnapshot().getCreateTime());
        return snapshot;
    }

    /**
     * 创建站点快照
     * @param hostId hostId
     * @return snapshot of the host
     */
    protected abstract HostCrawlingSnapshotDTO createCrawlingSnapshot(String hostId);

    /**
     * 向爬虫发送种子URL，开始爬取
     * @param host host
     */
    abstract void crawl(Host host);

    /**
     * 爬取完成操作
     * @param host host
     */
    void crawlingCompleted(Host host) {
        crawlingHostMap.remove(host.getHostId());
        controllerSupport.createSnapshot(host, HostState.Processing);
        System.out.println(host.getHostName() + "爬取完毕");
    }

    /**
     * 处理完毕操纵
     * @param host host
     */
    abstract void processingCompleted(Host host);

    private void addCrawlingHost(Host host) {
        if (isDistributed()) {
            crawlingHostMap.put(host.getHostId(), host);
        } else {
            lock.lock();
            try {
                crawlingHostMap.put(host.getHostId(), host);
            } finally {
                lock.unlock();
            }
        }
    }

    private class SchedulerThread implements Runnable {

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
            if (controlConfig.getParallelism() > 0) {
                threadPool = new CountableThreadPool(controlConfig.getParallelism());
            } else {
                threadPool = new CountableThreadPool(Integer.MAX_VALUE);
            }

            logger.info("站点调度器初始化完成，使用如下配置启动：");
            logger.info("Host Scheduler Name: {}", hostScheduler.getClass());
            logger.info("Host Crawling Parallelism: {}", controlConfig.getParallelism() > 0 ? controlConfig.getParallelism() : "all");
        }

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
                            controllerSupport.createSnapshot(host, HostState.Crawling);
                            addCrawlingHost(host);
                            logger.info("站点：{} 开始爬取......", host.getHostName());

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

}
