package com.paytm.digital.education.explore.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@Profile("local")
public class JedisStandAloneConfig {
    private final String host;
    private final int port;

    public JedisStandAloneConfig(@Value("${redis.port}") int port,
                                 @Value("${redis.host}") String host) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(this.host, this.port);
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        return connectionFactory;
    }
}
