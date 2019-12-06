package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.AbstractCacheLockStrategy;
import com.paytm.digital.education.service.CacheData;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static java.time.Duration.ofSeconds;

@Service
public class CompleteLockStrategy extends AbstractCacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);

    private final StringRedisTemplate template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int lockDurationForProcessInSeconds;

    public CompleteLockStrategy(
            @Qualifier("exploreRedisTemplate") StringRedisTemplate template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            @Value("${redis.cache.ttl.millis}") int ttlInMillis,
            CacheValueProcessor cacheValueProcessor) {
        super(logger, template, cacheValueProcessor, ttlInMillis);
        this.template = template;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
        this.cacheValueProcessor = cacheValueProcessor;
    }

    @Override
    public Object getCacheValue(
            String key, CachedMethod cachedMethod, boolean shouldCacheNull) {
        synchronized (key.intern()) {
            String lockKey = key + "::redisLock";
            for (int i = 0; i < 10; i++) {
                Boolean success = template.opsForValue()
                        .setIfAbsent(lockKey, "1", ofSeconds(lockDurationForProcessInSeconds));
                if (BooleanUtils.isNotTrue(success)) {
                    try {
                        Thread.sleep(500);
                        continue;
                    } catch (InterruptedException e) {
                        logger.error("error", e);
                    }
                }

                CacheData cacheData = cacheValueProcessor.parseCacheValue(template.opsForValue().get(key));
                Object oldData = null;
                if (cacheData.getExpiryDateTime() != null && cacheData.getData() != null) {
                    oldData = deSerializeData(cacheData.getData(), key, logger);
                }

                if (cacheData.getExpiryDateTime() != null && cacheData.getExpiryDateTime().isAfterNow()) {
                    template.opsForValue().getOperations().delete(lockKey);
                    return oldData;
                }

                return writeToCacheAndReturn(oldData, lockKey, key, cachedMethod, shouldCacheNull);
            }
            return null;
        }
    }
}

