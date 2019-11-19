package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.CacheLockStrategy;
import com.paytm.digital.education.service.RedisOrchestrator;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.paytm.digital.education.utility.SerializationUtils.fromHexString;
import static com.paytm.digital.education.utility.SerializationUtils.toHexString;

@Service
@Setter
public class RedisOrchestratorImpl implements RedisOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(RedisOrchestratorImpl.class);

    private final RedisTemplate<String, String> template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int ttl;
    private final CacheLockStrategy cacheLockStrategy;

    public RedisOrchestratorImpl(
            RedisTemplate<String, String> template,
            CacheValueProcessor cacheValueProcessor,
            @Value("${redis.cache.ttl.millis}") int ttl,
            @Qualifier("writeLockStrategy") CacheLockStrategy cacheLockStrategy) {
        this.template = template;
        this.cacheValueProcessor = cacheValueProcessor;
        this.ttl = ttl;
        this.cacheLockStrategy = cacheLockStrategy;
    }

    @Override
    public Object get(String key, CachedMethod cachedMethod) {
        return cacheLockStrategy.getCacheValue(key, cachedMethod);
    }
}
