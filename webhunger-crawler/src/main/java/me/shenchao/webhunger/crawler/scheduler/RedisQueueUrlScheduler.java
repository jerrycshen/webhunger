package me.shenchao.webhunger.crawler.scheduler;

import com.alibaba.fastjson.JSON;
import me.shenchao.webhunger.constant.RedisPrefixConsts;
import me.shenchao.webhunger.crawler.listener.SiteListener;
import me.shenchao.webhunger.crawler.selector.SiteSelector;
import me.shenchao.webhunger.entity.webmagic.Request;
import me.shenchao.webhunger.entity.webmagic.Site;
import me.shenchao.webhunger.util.common.MD5Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.LifeCycle;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * 基于Redis存储的URL调度器，每个站点使用FIFO队列管理URL顺序
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class RedisQueueUrlScheduler extends DuplicateRemovedScheduler implements SiteListener, DuplicateRemover {

    private JedisPool pool;

    private SiteSelector siteSelector;

    public RedisQueueUrlScheduler(SiteSelector siteSelector, JedisPool pool) {
        this.siteSelector = siteSelector;
        this.pool = pool;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, LifeCycle lifeCycle) {
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(RedisPrefixConsts.getQueueKey(request.getSiteId()), request.getUrl());
            String field = MD5Utils.get16bitMD5(request.getUrl());
            String value = JSON.toJSONString(request);
            jedis.hset(RedisPrefixConsts.getItemKey(request.getSiteId()), field, value);
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public boolean isDuplicate(Request request, LifeCycle task) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.sadd(RedisPrefixConsts.getSetKey(request.getSiteId()), request.getUrl()) == 0;
        } finally {
            pool.returnResource(jedis);
        }
    }


    @Override
    public void resetDuplicateCheck(LifeCycle task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTotalRequestsCount(LifeCycle lifeCycle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Request poll(LifeCycle task) {
        Jedis jedis = pool.getResource();
        try {
            Site site = siteSelector.select(this);
            String url;
            if (site == null || (url = jedis.lpop(RedisPrefixConsts.getQueueKey(site.getHost().getHostId()))) == null) {
                return null;
            }
            String field = MD5Utils.get16bitMD5(url);
            String value = jedis.hget(RedisPrefixConsts.getItemKey(site.getHost().getHostId()), field);
            if (value != null) {
                Request request = JSON.parseObject(value, Request.class);
                return request;
            }
            return null;
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getLeftRequestsCount(String siteId) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(RedisPrefixConsts.getQueueKey(siteId));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequestsCount(String siteId) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard(RedisPrefixConsts.getSetKey(siteId));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }
}
