package com.paytm.digital.education.config;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;

@Configuration
public class JedisConfiguration {
    private static final Logger    log                        = LoggerFactory.getLogger(JedisConfiguration.class);
    private static final Integer   DEFAULT_CONNECTION_TIMEOUT = 5000;
    private static final Integer   DEFAULT_POOL_SIZE          = 50;

    @Value("${redis.host}")
    private              String  redisHost;
    @Value("${redis.port}")
    private              String  redisPort;
    @Value("${redis.max_pool_size}")
    private              String  redisMaxPoolSize;
    @Value("${redis.connection_timeout_in_ms}")
    private              String  redisConnectionTimeoutInMs;
    private static       JedisPool jedisPool;

    @PostConstruct
    public void init() {
        try {
            Integer connTimeout = Objects.nonNull(this.redisConnectionTimeoutInMs)
                    ? Integer.valueOf(this.redisConnectionTimeoutInMs) :
                    DEFAULT_CONNECTION_TIMEOUT;
            JedisPoolConfig poolConfig = this.buildPoolConfig();
            poolConfig.setMaxWaitMillis(connTimeout);
            poolConfig.setTestWhileIdle(true);
            log.info("Redis Details : host : " + this.redisHost + " port : " + this.redisPort);
            Integer redisPortInt = Integer.parseInt(this.redisPort);
            jedisPool = new JedisPool(poolConfig, this.redisHost, redisPortInt, connTimeout);
        } catch (Exception ex) {
            log.error("Got exception while initializing redis ,Exception", ex);
        }

    }

    private JedisPoolConfig buildPoolConfig() {
        Integer poolSize = Objects.nonNull(this.redisMaxPoolSize)
                ? Integer.valueOf(this.redisMaxPoolSize) :
                DEFAULT_POOL_SIZE;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(poolSize);
        return poolConfig;
    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    @PreDestroy
    public void cleanUp() {
        jedisPool.destroy();
    }
}
