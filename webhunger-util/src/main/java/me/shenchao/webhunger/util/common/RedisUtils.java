package me.shenchao.webhunger.util.common;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis工具类
 */
public class RedisUtils {

    /**
     * TODO 设置配置参数
     * 初始化redis连接池
     *
     * @param redisAddress redis 连接地址
     * @return redis连接池
     */
    public static JedisPool initJedisPool(String redisAddress) {
        String host;
        int port;
        int index = redisAddress.indexOf(":");
        if (index == -1) {
            port = 6379;
            host = redisAddress;
        } else {
            String[] ss = redisAddress.split(":");
            host = ss[0];
            port = Integer.parseInt(ss[1]);
        }
        return new JedisPool(new JedisPoolConfig(), host, port);
    }
}
