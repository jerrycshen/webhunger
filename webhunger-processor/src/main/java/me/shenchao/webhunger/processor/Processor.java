package me.shenchao.webhunger.processor;

import me.shenchao.webhunger.client.api.processor.AbstractHostHandler;
import me.shenchao.webhunger.client.api.processor.AbstractPageHandler;
import me.shenchao.webhunger.dto.PageDTO;
import me.shenchao.webhunger.entity.HostHandlerConfig;
import me.shenchao.webhunger.entity.PageHandlerConfig;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.dominate.BaseHostDominate;
import me.shenchao.webhunger.processor.handler.NullHostHandler;
import me.shenchao.webhunger.processor.handler.NullPageHandler;
import me.shenchao.webhunger.processor.scheduler.PageScheduler;
import me.shenchao.webhunger.util.classloader.ThirdPartyClassLoader;
import me.shenchao.webhunger.util.common.SystemUtils;
import me.shenchao.webhunger.util.thread.CountableThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class Processor implements Runnable {

    private BaseHostDominate hostDominate;

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

    private PageHandlerChainFactory pageHandlerChainFactory = new PageHandlerChainFactory();

    private HostHandlerChainFactory hostHandlerChainFactory = new HostHandlerChainFactory();

    private int threadNum = 1;

    private Processor() {
    }

    public static Processor create() {
        return new Processor();
    }

    @Override
    public void run() {
        checkRunningStat();
        initComponent();
        logger.info("Processor {} 启动完成......", SystemUtils.getHostName());
        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            final PageDTO page = pageScheduler.poll();
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
        processPage(page, hostDominate.getHostMap().get(page.getHostId()));
    }

    public void processPage(PageDTO page, Host host) {
        AbstractPageHandler pageHandlerChain = pageHandlerChainFactory.getPageHandlerChain(host);
        pageHandlerChain.handle(page);
    }

    public void processHost(String hostId) {
        processHost(hostDominate.getHostMap().get(hostId));
    }

    public void processHost(Host host) {
        AbstractHostHandler hostHandlerChain = hostHandlerChainFactory.getHostHandlerChain(host);
        hostHandlerChain.handle(host);
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

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public Processor setPageScheduler(PageScheduler pageScheduler) {
        this.pageScheduler = pageScheduler;
        return this;
    }

    public void setHostDominate(BaseHostDominate hostDominate) {
        this.hostDominate = hostDominate;
    }

    private class PageHandlerChainFactory {

        private Map<String, AbstractPageHandler> pageHandlerChainMap = new HashMap<>();

        private AbstractPageHandler getPageHandlerChain(Host host) {
            String hostId = host.getHostId();
            if (pageHandlerChainMap.get(hostId) != null) {
                return pageHandlerChainMap.get(hostId);
            }
            synchronized (PageHandlerChainFactory.class) {
                if (pageHandlerChainMap.get(hostId) != null) {
                    return pageHandlerChainMap.get(hostId);
                } else {
                    AbstractPageHandler headHandler = buildPageHandlerChain(host);
                    pageHandlerChainMap.put(hostId, headHandler);
                    return headHandler;
                }
            }
        }

        private AbstractPageHandler buildPageHandlerChain(Host host) {
            PageHandlerConfig pageHandlerConfig = host.getHostConfig().getPageHandlerConfig();
            List<AbstractPageHandler> handlers = ThirdPartyClassLoader.loadClasses(pageHandlerConfig.getHandlerJarDir(), pageHandlerConfig.getHandlerClassList(), AbstractPageHandler.class);
            if (handlers.size() == 0) {
                return new NullPageHandler();
            }
            for (int i = 0; i < handlers.size() - 1; ++i) {
                handlers.get(i).setSuccessor(handlers.get(i + 1));
            }
            return handlers.get(0);
        }

    }

    private class HostHandlerChainFactory {

        private Map<String, AbstractHostHandler> hostHandlerChainMap = new HashMap<>();

        private AbstractHostHandler getHostHandlerChain(Host host) {
            String hostId = host.getHostId();
            if (hostHandlerChainMap.get(hostId) != null) {
                return hostHandlerChainMap.get(hostId);
            }
            synchronized (HostHandlerChainFactory.class) {
                if (hostHandlerChainMap.get(hostId) != null) {
                    return hostHandlerChainMap.get(hostId);
                } else {
                    AbstractHostHandler headHandler = buildHostHandlerChain(host);
                    hostHandlerChainMap.put(hostId, headHandler);
                    return headHandler;
                }
            }
        }

        private AbstractHostHandler buildHostHandlerChain(Host host) {
            HostHandlerConfig hostHandlerConfig = host.getHostConfig().getHostHandlerConfig();
            List<AbstractHostHandler> handlers = ThirdPartyClassLoader.loadClasses(hostHandlerConfig.getHandlerJarDir(), hostHandlerConfig.getHandlerClassList(), AbstractHostHandler.class);
            if (handlers.size() == 0) {
                return new NullHostHandler();
            }
            for (int i = 0; i < handlers.size() - 1; ++i) {
                handlers.get(i).setSuccessor(handlers.get(i + 1));
            }
            return handlers.get(0);
        }
    }

}
