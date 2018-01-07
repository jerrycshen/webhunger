package me.shenchao.webhunger.crawler.dominate;

import me.shenchao.webhunger.entity.Host;
import org.apache.zookeeper.ZooKeeper;
import us.codecraft.webmagic.Spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedSiteDominate extends BaseSiteDominate {

    /**
     * 分布式爬虫字段<br>
     * 站点爬取结束检测次数。如果<b>连续</b>检测{@code COMPLETED_CHECK_TIME}，则认定该站点在当前爬虫节点上
     * 爬取结束。否则，在还没有到达该临界值前，继续选择下一个站点进行爬取。如果某站点检测次数已经到达{@code COMPLETED_CHECK_TIME - i}
     * 次，但在下一次尝试获取该站点URL时，获取到了，那么会清空原来的计数，重新开始计数
     */
    private static final int COMPLETED_CHECK_TIME = 3;

    /**
     * 记录每个站点爬取结束检测次数
     */
    private Map<String, Integer> siteCompletedCheckCountMap = new HashMap<>();

    private ZooKeeper zooKeeper;

    public DistributedSiteDominate(Spider spider, ZooKeeper zooKeeper) {
        super(spider);
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void checkCrawledCompleted(String siteId) {
        int checkedTime = siteCompletedCheckCountMap.get(siteId);
        if (checkedTime < COMPLETED_CHECK_TIME) {
            siteCompletedCheckCountMap.put(siteId, checkedTime + 1);
        } else {
            // 已经到达检测次数临界值，可以认定该站点在本爬虫节点已经爬取完毕
            // 在本地待爬列表中删除该站点

            // 更新zookeeper中该节点的值 + 1
            // 由于可能多个爬虫节点同时更新该值，所以这里使用分布式锁控制并发操作
        }
    }

    public void updateLocalHostList(List<Host> hostList) {
        if (hostList.size() == 0) {
            return;
        }

    }
}
