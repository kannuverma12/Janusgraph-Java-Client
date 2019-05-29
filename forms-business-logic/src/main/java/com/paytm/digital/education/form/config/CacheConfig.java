package com.paytm.digital.education.form.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

//@Configuration
//@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final String AUTO_EXPIRE_CACHE = "AutoExpireCache";
    public static final String PERSONA_CACHE = "personaCache";

    @Value("${cache.guava.ttl}")
    private Integer ttl;

    @Value("${cache.guava.size}")
    private Integer size;

    @Override
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache cache1 = new CaffeineCache(AUTO_EXPIRE_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(ttl, TimeUnit.MINUTES)
                .maximumSize(size)
                .build());

        CaffeineCache cache2 = new CaffeineCache(PERSONA_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(ttl, TimeUnit.MINUTES)
                .maximumSize(size)
                .build());

        cacheManager.setCaches(Arrays.asList(cache1, cache2));

        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }
}