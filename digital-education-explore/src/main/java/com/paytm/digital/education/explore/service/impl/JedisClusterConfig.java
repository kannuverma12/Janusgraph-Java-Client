package com.paytm.digital.education.explore.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;

@Configuration
@Profile({"staging", "production"})
public class JedisClusterConfig {

    private final String host;
    private final int port;
    private final int  redisMaxPoolSize;
    private final int  redisConnectionTimeoutInMs;

    public JedisClusterConfig(@Value("${redis.port}") int port,
                                 @Value("${redis.host}") String host,
                                 @Value("${redis.max_pool_size:50}") int redisMaxPoolSize,
                                 @Value("${redis.connection_timeout_in_ms:5000}") int redisConnectionTimeoutInMs) {
        this.host = host;
        this.port = port;
        this.redisMaxPoolSize = redisMaxPoolSize;
        this.redisConnectionTimeoutInMs = redisConnectionTimeoutInMs;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig poolConfig = jedisPoolConfig();
        RedisClusterConfiguration redisClusterConfiguration
                = new RedisClusterConfiguration(Collections.singletonList(this.host + ":" + this.port));
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisClusterConfiguration, poolConfig);
        return connectionFactory;
    }

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisMaxPoolSize);
        poolConfig.setMaxWaitMillis(redisConnectionTimeoutInMs);
        poolConfig.setTestWhileIdle(true);
        return poolConfig;
    }
}
