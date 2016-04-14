package com.cosmos.data.redis.core;

import com.cosmos.data.redis.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.ShardedJedis;

import java.util.Map;

/**
 * Jedis Template
 *
 * 与Redis交互的核心类
 */
public class JedisTemplate {

    private static final Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

    @Autowired
    private JedisDataSource redisDataSource;

    /**
     * 有返回结果的回调接口定义。
     */
    private interface JedisAction<T> {
        T action(ShardedJedis shardedJedis);
    }

    /**
     * 无返回结果的回调接口定义。
     */
    private interface JedisActionNoResult {
        void action(ShardedJedis shardedJedis);
    }

    /**
     * 执行有返回结果的action。
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
     * 执行无返回结果的action。
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
     * 设置键值
     *
     * @param key   键
     * @param value 值
     * @return 返回状态
     */
    public String set(String key, String value) {
        return execute(shardedJedis -> {
            return shardedJedis.set(key, value);
        });
    }

    /**
     * 获取键值
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.get(key);
        });
    }

    /**
     * 自增键值, 若键不存在, 则键值初始化为0
     *
     * @param key 键
     * @return 自增后的值
     */
    public Long incr(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.incr(key);
        });
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean exists(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.exists(key);
        });
    }

    /**
     * 判断键的类型
     *
     * @param key 键
     * @return 类型
     */
    public String type(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.type(key);
        });
    }

    /**
     * 设置键的失效时间
     *
     * @param key     键
     * @param seconds 失效时间, 单位: 秒
     * @return 是否设置成功
     */
    public Long expire(String key, int seconds) {
        return execute(shardedJedis -> {
            return shardedJedis.expire(key, seconds);
        });
    }

    /**
     * 设置键在某个时间点失效
     *
     * @param key      键
     * @param unixTime 失效时间点
     * @return 是否设置成功
     */
    public Long expireAt(String key, long unixTime) {
        return execute(shardedJedis -> {
            return shardedJedis.expireAt(key, unixTime);
        });
    }

    /**
     * 获取键的失效时间
     *
     * @param key 键
     * @return 失效时间
     */
    public Long ttl(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.ttl(key);
        });
    }

    /**
     * 在键的偏移位设置值
     *
     * @param key    键
     * @param offset 偏移位置
     * @param value  值, 0/1
     * @return 该位置的原值
     */
    public Boolean setbit(String key, long offset, boolean value) {
        return execute(shardedJedis -> {
            return shardedJedis.setbit(key, offset, value);
        });
    }

    /**
     * 获取键所在偏移位的值
     *
     * @param key    键
     * @param offset 偏移位置
     * @return 偏移位的值
     */
    public Boolean getbit(String key, long offset) {
        return execute(shardedJedis -> {
            return shardedJedis.getbit(key, offset);
        });
    }

    /**
     * 在键的偏移位设置新值
     *
     * @param key    键
     * @param offset 偏移位置
     * @param value  值
     * @return 修改后值的长度
     */
    public Long setrange(String key, long offset, String value) {
        return execute(shardedJedis -> {
            return shardedJedis.setrange(key, offset, value);
        });
    }

    /**
     * 获取偏移开始位置至结束位置的值
     *
     * @param key         键
     * @param startOffset 偏移开始位置
     * @param endOffset   偏移结束位置
     * @return 部分值
     */
    public String getrange(String key, long startOffset, long endOffset) {
        return execute(shardedJedis -> {
            return shardedJedis.getrange(key, startOffset, endOffset);
        });
    }

    /**
     * 设置哈希键中的某一个域的值
     *
     * @param key   键
     * @param field 域
     * @param value 值
     * @return 1, 新增 / 0, 更新
     */
    public Long hset(String key, String field, String value) {
        return execute(shardedJedis -> {
            return shardedJedis.hset(key, field, value);
        });
    }

    /**
     * 获取哈希键中的所有域值
     *
     * @param key 键
     * @return 哈希键中的所有域值
     */
    public Map<String, String> hgetall(String key) {
        return execute(shardedJedis -> {
            return shardedJedis.hgetAll(key);
        });
    }
}
