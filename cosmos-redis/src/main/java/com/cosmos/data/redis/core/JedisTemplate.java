package com.cosmos.data.redis.core;

import com.cosmos.data.redis.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.ShardedJedis;

import java.util.Map;

/**
 * Jedis Template which is the core class that interact with redis.
 *
 * @author BSD
 */
public class JedisTemplate {

    private static final Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

    @Autowired
    private JedisDataSource redisDataSource;

    /**
     * Jedis action that has a return value.
     */
    private interface JedisAction<T> {
        T action(ShardedJedis shardedJedis);
    }

    /**
     * Jedis action that has no return value.
     */
    private interface JedisActionNoResult {
        void action(ShardedJedis shardedJedis);
    }

    /**
     * Execute redis action which has a return value.
     *
     * @param jedisAction redis action
     * @param <T> return type
     * @return return value
     */
    private <T> T execute(JedisAction<T> jedisAction) {
        ShardedJedis shardedJedis = null;
        boolean broken = false;
        try {
            shardedJedis = redisDataSource.getRedisClient();
            return jedisAction.action(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis action failed!", e);
            broken = true;
            throw e;
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
        }
    }

    /**
     * Execute redis action which has no return value.
     *
     * @param jedisAction redis action
     */
    private void execute(JedisActionNoResult jedisAction) {
        ShardedJedis shardedJedis = null;
        boolean broken = false;
        try {
            shardedJedis = redisDataSource.getRedisClient();
            jedisAction.action(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis action failed!", e);
            broken = true;
            throw new RedisException("No");
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
        }
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
     * GB).
     *
     * @param key key
     * @param value value
     * @return Status code reply
     */
    public String set(String key, String value) {
        return execute(shardedJedis -> {
            return shardedJedis.set(key, value);
        });
    }

    /**
     * Get the value of the specified key. If the key does not exist null is returned. If the value
     * stored at key is not a string an error is returned because GET can only handle string values.
     *
     * @param key key
     * @return Bulk reply
     */
    public String get(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.get(key);
        });
    }

    /**
     * Increment the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the increment operation.
     *
     * INCR commands are limited to 64 bit signed integers.
     *
     * @param key key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     */
    public Long incr(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.incr(key);
        });
    }

    /**
     * Test if the specified key exists. The command returns "1" if the key exists, otherwise "0" is
     * returned. Note that even keys set with an empty string as value will return "1".
     *
     * @param key key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public Boolean exists(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.exists(key);
        });
    }

    /**
     * Return the type of the value stored at key in form of a string. The type can be one of "none",
     * "string", "list", "set". "none" is returned if the key does not exist.
     *
     * @param key key
     * @return Status code reply, specifically: "none" if the key does not exist "string" if the key
     *         contains a String value "list" if the key contains a List value "set" if the key
     *         contains a Set value "zset" if the key contains a Sorted Set value "hash" if the key
     *         contains a Hash value
     */
    public String type(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.type(key);
        });
    }

    /**
     * Set a timeout on the specified key. After the timeout the key will be automatically deleted by
     * the server. A key with an associated timeout is said to be volatile in Redis terminology.
     *
     * @param key key
     * @param seconds expire timeout
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     *         the key already has an associated timeout
     */
    public Long expire(String key, int seconds) {
        return execute(shardedJedis -> {
            return shardedJedis.expire(key, seconds);
        });
    }

    /**
     * EXPIREAT works exctly like {@link #expire(String, int) EXPIRE} but instead to get the number of
     * seconds representing the Time To Live of the key as a second argument (that is a relative way
     * of specifing the TTL), it takes an absolute one in the form of a UNIX timestamp (Number of
     * seconds elapsed since 1 Gen 1970).
     *
     * @param key key
     * @param unixTime expire timestamp
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     *         the key already has an associated timeout
     */
    public Long expireAt(String key, long unixTime) {
        return execute(shardedJedis -> {
            return shardedJedis.expireAt(key, unixTime);
        });
    }

    /**
     * The TTL command returns the remaining time to live in seconds of a key that has an
     * {@link #expire(String, int) EXPIRE} set. This introspection capability allows a Redis client to
     * check how many seconds a given key will continue to be part of the data set.
     *
     * @param key key
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an
     *         EXPIRE
     */
    public Long ttl(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.ttl(key);
        });
    }

    /**
     * Sets or clears the bit at offset in the string value stored at key.
     *
     * @param key key
     * @param offset offset
     * @param value true for set, false for clear
     * @return
     */
    public Boolean setbit(String key, long offset, boolean value) {
        return execute(shardedJedis -> {
            return shardedJedis.setbit(key, offset, value);
        });
    }

    /**
     * Returns the bit value at offset in the string value stored at key.
     *
     * @param key key
     * @param offset offset
     * @return true for set, false for unset
     */
    public Boolean getbit(String key, long offset) {
        return execute(shardedJedis -> {
            return shardedJedis.getbit(key, offset);
        });
    }

    /**
     * Set new value at offset in value.
     *
     * @param key key
     * @param offset offset
     * @param value new value
     * @return the length of key after change
     */
    public Long setrange(String key, long offset, String value) {
        return execute(shardedJedis -> {
            return shardedJedis.setrange(key, offset, value);
        });
    }

    /**
     * Get partial value from start offset to end offset of the key.
     *
     * @param key key
     * @param startOffset start offset
     * @param endOffset end offset
     * @return partial value
     */
    public String getrange(String key, long startOffset, long endOffset) {
        return execute(shardedJedis -> {
            return shardedJedis.getrange(key, startOffset, endOffset);
        });
    }

    /**
     * Set the specified hash field to the specified value.
     *
     * If key does not exist, a new key holding a hash is created.
     *
     * @param key key
     * @param field field
     * @param value value
     * @return If the field already exists, and the HSET just produced an update of the value, 0 is
     *         returned, otherwise if a new field is created 1 is returned.
     */
    public Long hset(String key, String field, String value) {
        return execute(shardedJedis -> {
            return shardedJedis.hset(key, field, value);
        });
    }

    /**
     * Return all the fields and associated values in a hash.
     *
     * @param key key
     * @return All the fields and values contained into a hash.
     */
    public Map<String, String> hgetall(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.hgetAll(key);
        });
    }
}
