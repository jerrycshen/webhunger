package me.shenchao.webhunger.util.common;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
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

        private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

        private static final String SUB_NODE = "lock_";

        private ZooKeeper zooKeeper;

        private String lockNode;

        private String myZNode;

        private String waitZNode;

        private CountDownLatch latch;

        public DistributedLock(ZooKeeper zooKeeper, String lockNode) {
            this.zooKeeper = zooKeeper;
            this.lockNode = lockNode;
        }

        public void lock() {
            try {
                if (!tryLock()) {
                    waitForLock(waitZNode);
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void waitForLock(String waitZNode) throws KeeperException, InterruptedException {
            Stat stat = zooKeeper.exists(waitZNode, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (latch != null) {
                        latch.countDown();
                    }
                }
            });
            // 等待前一个节点释放锁
            if (stat != null) {
                latch = new CountDownLatch(1);
                latch.await();
                latch = null;
            }
        }

        public void unlock() {
            try {
                zooKeeper.delete(myZNode, -1);
                myZNode = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }

        public boolean tryLock() {
            List<String> subNodes = null;
            try {
                myZNode = zooKeeper.create(lockNode + "/" + SUB_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                logger.debug("{} is created", myZNode);
                // 取出所有子节点，不要watch
                subNodes = zooKeeper.getChildren(lockNode, false);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
            }
            if (subNodes == null || subNodes.size() == 0) {
                throw new RuntimeException("zookeeper连接出现问题......");
            }
            // 如果列表中只有一个子节点，那肯定就是自己获得锁
            if (subNodes.size() == 1) {
                return true;
            }
            Collections.sort(subNodes);
            String myZNodeName = myZNode.substring((lockNode + "/").length());
            int index = Collections.binarySearch(subNodes, myZNodeName);
            if (index == 0) {
                // 表明当前节点处于第一位，可以获得锁
                return true;
            }
            if (index > 0) {
                waitZNode = lockNode + "/" + subNodes.get(index - 1);
            }
            return false;
        }

    }
}
