package com.cosmos.data.redis.core;

import com.cosmos.data.redis.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Jedis Data Source
 */
public class JedisDataSource {

    private static final Logger logger = LoggerFactory.getLogger(JedisDataSource.class);

    private ShardedJedisPool shardedJedisPool;

    public JedisDataSource(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    /**
     * @return Redis客户端对象
     */
    public ShardedJedis getRedisClient() {
        try {
            return shardedJedisPool.getResource();
        } catch (Exception e) {
            logger.error("[{}] getRedisClient error!", JedisDataSource.class.getSimpleName());
            throw new RedisException("getRedisClient error!", e);
        }
    }

    public void returnResource(ShardedJedis shardedJedis) {
        if(shardedJedis != null) {
            shardedJedis.close();
        }
    }

    public void returnResource(ShardedJedis shardedJedis, boolean broken) {
        if(shardedJedis != null) {
            shardedJedis.close();
        }
    }

}
