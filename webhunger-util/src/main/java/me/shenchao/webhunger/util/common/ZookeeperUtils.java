package me.shenchao.webhunger.util.common;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 操作类
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class ZookeeperUtils {

    /**
     * zk 客户端超时时间
     */
    private static final int ZK_SESSION_TIMEOUT = 10000;

    /**
     * 注意这里获取一个连接后，客户端一直与集群相连，所以需谨慎获取连接<br>
     *
     *   该方法一定会获取到一个有效的连接，否则一直会阻塞重试
     */
    public static ZooKeeper getZKConnection(String zkServerAddresses) {
        ZooKeeper zooKeeper = null;
        try {
            CountDownLatch latch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(zkServerAddresses, ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            // 阻塞直到连接成功
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }

    /**
     * 分布式锁实现
     */
    public static class DistributedLock {

    }
}
