package com.cosmos.data.redis.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Redis
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/applicationContext-redis.xml"})
public class JedisTemplateTest {

    private static final Logger logger = LoggerFactory.getLogger(JedisTemplateTest.class);

    @Autowired
    private JedisTemplate jedisTemplate;

    /**
     * 批量递增操作
     */
    @Test
    public void testBulkOps() {
        for(int i = 0; i < 100000; i++) {
            jedisTemplate.incr("test-bulk-ops");
        }
    }
}
