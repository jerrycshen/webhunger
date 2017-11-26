package me.shenchao.webhunger.control.controller;

import me.shenchao.webhunger.config.ControlConfig;
import me.shenchao.webhunger.control.scheduler.HostScheduler;
import me.shenchao.webhunger.control.scheduler.QueueHostScheduler;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单机版站点调度控制器
 *
 * @author Jerry Shen
 * @since 0.1
 */
class StandaloneController extends MasterController {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneController.class);

    private HostScheduler hostScheduler;

    StandaloneController(ControlConfig controlConfig) {
        super(controlConfig);
    }

    class SchedulerThread implements Runnable {

        @Override
        public void run() {

            initComponent();
        }
    }

    /**
     * 初始化控制相关组件
     */
    private void initComponent() {
        // TODO 根据配置设置站点调度器，默认采用QueueHostScheduler
        if (hostScheduler == null) {
            hostScheduler = new QueueHostScheduler();
        }
//        if (threadPool == null) {
//            threadPool = new CountableThreadPool(crawlersNum);
//        }
    }

    /**
     * 爬取指定站点
     * @param hostId host_id
     */
    public void start(String hostId) {
        Host host = hostMap.get(hostId);
        // 清理数据，准备环境 todo
//        crawlersControlSupport.rollbackHost(host);
        // 修改host状态
        host.setState(HostState.Waiting.getState());
        // 生成host快照，记录当前状态情况
        controllerSupport.createSnapshot(host);
        hostScheduler.push(host);
//        crawlersControlSupport.markHostWaitingState(host);
//        signalNewHost();
    }

    public void setHostScheduler(HostScheduler hostScheduler) {
        this.hostScheduler = hostScheduler;
    }
}
