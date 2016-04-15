package com.cosmos.data.redis.core;

import com.cosmos.data.redis.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Jedis Data Source.
 *
 * @author BSD
 */
public class JedisDataSource {

    private static final Logger logger = LoggerFactory.getLogger(JedisDataSource.class);

    private ShardedJedisPool shardedJedisPool;

    public JedisDataSource(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    /**
     * Get {@link ShardedJedis} from {@link ShardedJedisPool}.
     *
     * @return {@link ShardedJedis} instance
     */
    public ShardedJedis getRedisClient() {
        try {
            return shardedJedisPool.getResource();
        } catch (Exception e) {
            logger.error("[{}] getRedisClient error!", JedisDataSource.class.getSimpleName());
            throw new RedisException("getRedisClient error!", e);
        }
    }

    /**
     * Return {@link ShardedJedis} to {@link ShardedJedisPool}.
     *
     * @param shardedJedis {@link ShardedJedis} instance
     */
    public void returnResource(ShardedJedis shardedJedis) {
        if(shardedJedis != null) {
            shardedJedis.close();
        }
    }

    /**
     * Return {@link ShardedJedis} to {@link ShardedJedisPool} with input parameter.
     *
     * @param shardedJedis {@link ShardedJedis} instance
     * @param broken whether the link is broken
     */
    public void returnResource(ShardedJedis shardedJedis, boolean broken) {
        if(shardedJedis != null) {
            shardedJedis.close();
        }
    }

}
