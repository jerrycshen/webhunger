package me.shenchao.webhunger.crawler.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import me.shenchao.webhunger.constant.RedisPrefixConsts;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.entity.SiteStatusStatistics;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分布式爬虫监听器<br>
 *
 *   与单机爬取不同的时候，站点在分布式爬取中的统计数据都会存放在redis中。考虑到
 *   爬取速度很快，同时应用的实时性要求又不是特别高，所以设计成定时向redis更新数据，
 *   会先在每个爬虫节点缓存一部分数据，然后一齐上传
 *
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class DistributedSpiderListener extends BaseSpiderListener {

    private JedisPool pool;

    private Map<String, LastUpdateSiteStatusStatistics> lastUpdateStatisticsMap = Maps.newHashMap();

    public DistributedSpiderListener(JedisPool pool) {
        this.pool = pool;
        Thread thread = new Thread(new UpdateRemoteStatisticsThread());
        thread.setDaemon(true);
        thread.start();
    }

    private List<UpdateData> update() {
        List<UpdateData> list = new ArrayList<>();
        /*
         * 这里依赖concurrentHashMap的弱一致性：即使在遍历的过程中，向其添加元素也不会报错，只是
         * 牺牲了一些可见性，但是由于该方法是被循环调用的，所以不会受到影响
         */
        for (Map.Entry<String, SiteStatusStatistics> entry : siteStatusStatisticsMap.entrySet()) {
            LastUpdateSiteStatusStatistics lastUpdateSiteStatusStatistics = lastUpdateStatisticsMap
                    .computeIfAbsent(entry.getKey(), k -> new LastUpdateSiteStatusStatistics(0, 0, 0));
            list.add(new UpdateData().update(entry.getKey(), entry.getValue(), lastUpdateSiteStatusStatistics));
        }
        return list;
    }

    /**
     * 更新远程redis统计信息的线程
     */
    private class UpdateRemoteStatisticsThread implements Runnable {

        private final int UPDATE_INTERVAL = 8000;

        @Override
        public void run() {
            while (true) {
                Jedis jedis = pool.getResource();
                try {
                    Pipeline pipeline = jedis.pipelined();
                    for (UpdateData updateData : update()) {
                        pipeline.hincrBy(RedisPrefixConsts.getCountKey(updateData.hostId),
                            RedisPrefixConsts.COUNT_SUCCESS_NUM, updateData.incrSuccessPageNum
                        );
                        pipeline.hincrBy(RedisPrefixConsts.getCountKey(updateData.hostId),
                            RedisPrefixConsts.COUNT_ERROR_NUM, updateData.incrErrorPageNum
                        );
                        for (ErrorPageDTO errorPageDTO : updateData.incrErrorPage) {
                            pipeline.rpush(RedisPrefixConsts.getErrorPrefix(updateData.hostId), JSON.toJSONString(errorPageDTO));
                        }
                    }
                    pipeline.sync();
                } finally {
                    pool.returnResource(jedis);
                }
                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {}
            }
        }
    }

    /**
     * 本次需要更新的信息
     */
    private class UpdateData {
        private String hostId;
        private int incrSuccessPageNum;
        private int incrErrorPageNum;
        private List<ErrorPageDTO> incrErrorPage;

        UpdateData update(String hostId, SiteStatusStatistics currentSiteStatistics, LastUpdateSiteStatusStatistics lastUpdateSiteStatusStatistics) {
            this.hostId = hostId;
            this.incrSuccessPageNum = currentSiteStatistics.getSuccessPageNum().get() - lastUpdateSiteStatusStatistics.successPageNum;
            this.incrErrorPageNum = currentSiteStatistics.getErrorPageNum().get() - lastUpdateSiteStatusStatistics.errorPageNum;
            int currentSize = currentSiteStatistics.getErrorRequests().size();
            int lastSize = lastUpdateSiteStatusStatistics.size;
            incrErrorPage = new ArrayList<>(currentSize - lastSize);
            for (int i = lastSize; i < currentSize; ++i) {
                incrErrorPage.add(currentSiteStatistics.getErrorRequests().get(i));
            }
            lastUpdateStatisticsMap.put(hostId, new LastUpdateSiteStatusStatistics(
               currentSiteStatistics.getSuccessPageNum().get(),
                    currentSiteStatistics.getErrorPageNum().get(), currentSize
            ));
            return this;
        }
    }

    /**
     * 上一次上传时的统计信息
     */
    private static class LastUpdateSiteStatusStatistics {
        private int successPageNum;
        private int errorPageNum;
        /**
         * error page 列表中上一次的size
         */
        private int size;

        public LastUpdateSiteStatusStatistics(int successPageNum, int errorPageNum, int size) {
            this.successPageNum = successPageNum;
            this.errorPageNum = errorPageNum;
            this.size = size;
        }
    }
}
