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

import static com.paytm.digital.education.constant.CommonConstants.REDIS_LOCK_POSTFIX;
import static java.time.Duration.ofSeconds;

@Service
public class WriteLockStrategy extends AbstractCacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);

    private final StringRedisTemplate template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int lockDurationForProcessInSeconds;

    public WriteLockStrategy(
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
    /** TODOs:-
     * 1. Replace intern with concurrent maps.
     *    See:- https://stackoverflow.com/questions/133988/synchronizing-on-string-objects-in-java#answer-134154
     * 2. Add lock token in redis so that processes don't remove other processes' locks
     * 3. More robust exception handling. At this point this service swallows most kind of exception.
     *    What if the cached method actually wants to process certain kind of exceptions.
     *    One option is that the cached methods can declare exceptions which it (cached method) wants to handle.
     *    This service shouldn't swallow those exceptions
     */
    public Object getCacheValue(
            String key, CachedMethod cachedMethod, boolean shouldCacheNull) {
        CacheData cacheData = cacheValueProcessor.parseCacheValue(template.opsForValue().get(key));
        Object oldData = null;
        if (cacheData.getExpiryDateTime() != null && cacheData.getData() != null) {
            oldData = deSerializeData(cacheData.getData(), key, logger);
        }

        if (cacheData.getExpiryDateTime() != null && cacheData.getExpiryDateTime().isAfterNow()) {
            return oldData;
        }

        logger.debug("Old cache value for key ", key);

        synchronized (key.intern()) {
            String lockKey = key + REDIS_LOCK_POSTFIX;
            Boolean success = template.opsForValue()
                    .setIfAbsent(lockKey, "1", ofSeconds(lockDurationForProcessInSeconds));
            if (BooleanUtils.isNotTrue(success)) {
                return oldData;
            }
            return writeToCacheAndReturn(oldData, lockKey, key, cachedMethod, shouldCacheNull);
        }
    }
}
