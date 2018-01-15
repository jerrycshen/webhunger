package me.shenchao.webhunger.processor;

import me.shenchao.webhunger.dto.PageDTO;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.scheduler.PageScheduler;
import me.shenchao.webhunger.util.common.SystemUtils;
import me.shenchao.webhunger.util.thread.CountableThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 页面处理器类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class Processor implements Runnable, LifeCycle {

    private Logger logger = LoggerFactory.getLogger(Processor.class);

    private AtomicInteger stat = new AtomicInteger(STAT_INIT);

    private boolean destroyWhenExit = true;

    private final static int STAT_INIT = 0;

    private final static int STAT_RUNNING = 1;

    private final static int STAT_STOPPED = 2;

    private CountableThreadPool threadPool;

    private PageScheduler pageScheduler;

    private ReentrantLock newPageLock = new ReentrantLock();

    private Condition newPageCondition = newPageLock.newCondition();

    private Thread asyncThread;

    private int threadNum = 1;

    private Date startTime;

    @Override
    public void run() {
        checkRunningStat();
        initComponent();
        logger.info("Processor {} 启动完成......", SystemUtils.getHostName());
        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            final PageDTO page = pageScheduler.poll(this);
            if (page == null) {
                waitNewPage();
            } else {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        processPage(page);
                    }
                });
            }
        }
        stat.set(STAT_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
        logger.info("Processor {} closed!", SystemUtils.getHostName());
    }

    private void processPage(PageDTO page) {

    }

    private void waitNewPage() {
        newPageLock.lock();
        try {
            newPageCondition.await();
        } catch (InterruptedException e) {
            logger.warn("waitNewPage - interrupted, error {}", e);
        } finally {
            newPageLock.unlock();
        }
    }

    public void signalNewPage() {
        try {
            newPageLock.lock();
            newPageCondition.signalAll();
        } finally {
            newPageLock.unlock();
        }
    }

    public void runAsync() {
        asyncThread = new Thread(this);
        asyncThread.setDaemon(false);
        asyncThread.start();
    }

    private void initComponent() {
        threadPool = new CountableThreadPool(threadNum);
        startTime = new Date();
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Processor is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    private void close() {
        destroyEach(pageScheduler);
        threadPool.shutdown();
    }

    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Host> getSites() {
        return null;
    }

    public Processor setPageScheduler(PageScheduler pageScheduler) {
        this.pageScheduler = pageScheduler;
        return this;
    }
}
