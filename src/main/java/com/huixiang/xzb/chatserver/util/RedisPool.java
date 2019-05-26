package com.huixiang.xzb.chatserver.util;

import com.huixiang.xzb.chatserver.configuration.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool = null;
    static {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(Configuration.REDIS_MAX_TOTAL);
            poolConfig.setMaxIdle(Configuration.REDIS_MAX_IDLE);
            pool = new JedisPool(poolConfig, Configuration.REDIS_HOST, Configuration.REDIS_PORT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
