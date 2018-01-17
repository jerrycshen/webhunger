package me.shenchao.webhunger.processor.dominate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shenchao.webhunger.constant.ZookeeperPathConsts;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.processor.Processor;
import me.shenchao.webhunger.processor.listener.HostPageNumListener;
import me.shenchao.webhunger.util.common.ZookeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 页面处理站点管理器
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class BaseHostDominate {

    /**
     * 记录站店ID与站点之间的映射关系
     */
    private Map<String, Host> hostMap = Maps.newConcurrentMap();

    /**
     * 记录所有处于页面处理阶段的站点
     */
    private volatile List<Host> hostList = Lists.newCopyOnWriteArrayList();

    private HostProcessedCompletedCheckThread hostProcessedCompletedCheckThread;

    private ExecutorService hostProcessExecutor;

    private Processor processor;

    private ZooKeeper zooKeeper;

    public BaseHostDominate(Processor processor, ZooKeeper zooKeeper) {
        this.processor = processor;
        this.zooKeeper = zooKeeper;
        hostProcessExecutor = Executors.newSingleThreadExecutor();
        hostProcessedCompletedCheckThread = new HostProcessedCompletedCheckThread();
        Thread thread = new Thread(hostProcessedCompletedCheckThread);
        thread.setDaemon(true);
        thread.start();
    }

    public void updateLocalProcessingHostList(List<Host> newSiteList) {
        for (Host host : newSiteList) {
            String hostId = host.getHostId();
            if (hostMap.get(hostId) == null) {
                addHost(host);
            }
        }
        processor.signalNewPage();
    }

    public List<Host> getHostList() {
        return hostList;
    }

    public Map<String, Host> getHostMap() {
        return hostMap;
    }

    public void checkProcessedCompleted(String hostId, HostPageNumListener hostListener) {
        // 将站点从待处理站点列表移除，但并没有真正移除该站点在map中的缓存，因为之后可能会恢复
        removeHostFromList(hostId);
        // 加入检测线程定时检查
        hostProcessedCompletedCheckThread.check(hostId, hostListener);
    }

    private void removeHostFromList(String hostId) {
        Host host = hostMap.get(hostId);
        hostList.remove(host);
    }

    private void addHostToList(String hostId) {
        Host host = hostMap.get(hostId);
        hostList.add(host);
    }

    private void removeHost(String hostId) {
        Host host = hostMap.get(hostId);
        hostMap.remove(hostId);
        hostList.remove(host);
    }

    private void addHost(Host host) {
        hostMap.put(host.getHostId(), host);
        hostList.add(host);
    }

    private void complete(String hostId) {
        removeHost(hostId);
        hostProcessedCompletedCheckThread.remove(hostId);

        // 更新zookeeper中该节点的值 + 1
        // 由于可能多个页面处理节点同时更新该值，所以这里使用分布式锁控制并发操作
        ZookeeperUtils.DistributedLock distributedLock = new ZookeeperUtils.DistributedLock(zooKeeper, ZookeeperPathConsts.PROCESSOR_LOCK);

        boolean unlocked = distributedLock.tryLock();
        try {
            if (unlocked) {
                try {
                    Stat exists = zooKeeper.exists(ZookeeperPathConsts.PROCESSING_HOST + "/" + hostId, false);
                    if (exists != null) {
                        hostProcessExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    processor.processHost(hostId);
                                    byte[] dataBytes = zooKeeper.getData(ZookeeperPathConsts.PROCESSING_HOST + "/" + hostId, false, null);
                                    int value = Integer.parseInt(new String(dataBytes));
                                    zooKeeper.setData(ZookeeperPathConsts.PROCESSING_HOST + "/" + hostId, String.valueOf(value + 1).getBytes(), -1);
                                } catch (KeeperException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            distributedLock.unlock();
        }
    }

    private boolean isHostCrawledCompleted(String hostId) {
        try {
            Stat exists = zooKeeper.exists(ZookeeperPathConsts.CRAWLING_HOST + "/" + hostId, false);
            return exists == null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void resumeHost(String hostId) {
        hostProcessedCompletedCheckThread.remove(hostId);
        addHostToList(hostId);
        processor.signalNewPage();
    }

    private class HostProcessedCompletedCheckThread implements Runnable {

        /**
         * 线程检测间隔
         */
        private final long checkInterval = 3000L;

        /**
         * 记录站点下一次检查的时间
         */
        private Map<String, Long> nextCheckTimeMap = new HashMap<>();

        private Map<String, HostPageNumListener> hostListenerMap = new HashMap<>();

        private Map<String, Long> delayTimeMap = new HashMap<>();

        private ReentrantLock lock = new ReentrantLock();

        @Override
        public void run() {
            while (true) {
                List<String> resumeList = new ArrayList<>();
                List<String> completedList = new ArrayList<>();
                lock.lock();
                try {
                    for (Map.Entry<String, Long> entry : nextCheckTimeMap.entrySet()) {
                        long currentTime = System.currentTimeMillis();
                        // 只会检查到达检测时间点的站点
                        if (currentTime > entry.getValue()) {
                            int leftPageNum = hostListenerMap.get(entry.getKey()).getLeftPagesNum(entry.getKey());
                            if (leftPageNum == 0) {
                                if (isHostCrawledCompleted(entry.getKey())) {
                                    // 如果同时站点已经爬取结束，意味着不会再生产新页面，那么认为可以结束对该站点的页面处理
                                    completedList.add(entry.getKey());
                                } else {
                                    // 否则推迟下一次的检测时间，推迟时间为上一次的两倍
                                    long nextDelayTime = delayTimeMap.get(entry.getKey()) * 2;
                                    nextCheckTimeMap.put(entry.getKey(), currentTime + nextDelayTime);
                                    delayTimeMap.put(entry.getKey(), nextDelayTime);
                                }
                            } else {
                                // 发现了新待处理页面，表明之前是假死，那么假如恢复队列
                                resumeList.add(entry.getKey());
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
                for (String resumeHostId : resumeList) {
                    resumeHost(resumeHostId);
                }
                for (String completedHostId : completedList) {
                    complete(completedHostId);
                }
                try {
                    Thread.sleep(checkInterval);
                } catch (InterruptedException ignored) {}
            }
        }

        private void check(String hostId, HostPageNumListener hostListener) {
            lock.lock();
            try {
                nextCheckTimeMap.put(hostId, System.currentTimeMillis() + 2000L);
                hostListenerMap.put(hostId, hostListener);
                delayTimeMap.put(hostId, 2000L);
            } finally {
                lock.unlock();
            }
        }

        private void remove(String hostId) {
            lock.lock();
            try {
                nextCheckTimeMap.remove(hostId);
                hostListenerMap.remove(hostId);
                delayTimeMap.remove(hostId);
            } finally {
                lock.unlock();
            }
        }
    }
}
