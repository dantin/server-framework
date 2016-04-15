package com.cosmos.data.redis.client;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory bean of {@link JedisCluster}.
 *
 * NOTE: Redis version >= 3.0, no production testing.
 */
public class JedisClusterFactoryBean implements FactoryBean<JedisCluster>, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisClusterFactoryBean.class);

    private static final String COLON = ":";
    private GenericObjectPoolConfig genericObjectPoolConfig;
    private JedisCluster jedisCluster;
    private int connectionTimeout = 2000;
    private int soTimeout = 3000;
    private int maxRedirections = 5;
    private Set<String> jedisClusterNodes;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (jedisClusterNodes == null || jedisClusterNodes.size() == 0) {
            logger.error("jedisClusterNodes is null.");
            throw new NullPointerException("jedisClusterNodes is null.");
        }
        Set<HostAndPort> haps = new HashSet<>();
        for (String node : jedisClusterNodes) {
            String[] arr = node.split(COLON);
            if (arr.length != 2) {
                logger.error("node address error!");
                throw new ParseException("node address error!", node.length() - 1);
            }
            haps.add(new HostAndPort(arr[0], Integer.valueOf(arr[1])));
        }
        jedisCluster = new JedisCluster(haps, connectionTimeout, soTimeout, maxRedirections, genericObjectPoolConfig);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("close redis cluster");
        jedisCluster.close();
    }

    @Override
    public JedisCluster getObject() throws Exception {
        return jedisCluster;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public GenericObjectPoolConfig getGenericObjectPoolConfig() {
        return genericObjectPoolConfig;
    }

    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public Set<String> getJedisClusterNodes() {
        return jedisClusterNodes;
    }

    public void setJedisClusterNodes(Set<String> jedisClusterNodes) {
        this.jedisClusterNodes = jedisClusterNodes;
    }
}
