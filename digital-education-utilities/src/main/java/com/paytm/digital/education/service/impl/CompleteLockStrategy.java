package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.AbstractCacheLockStrategy;
import com.paytm.digital.education.service.CacheData;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static java.time.Duration.ofSeconds;

@Service
public class CompleteLockStrategy extends AbstractCacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);

    private final RedisTemplate<String, String> template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int lockDurationForProcessInSeconds;
    private final int ttlInMillis;

    public CompleteLockStrategy(
            @Qualifier("exploreRedisTemplate") RedisTemplate<String, String> template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            @Value("${redis.cache.ttl.millis}") int ttlInMillis,
            CacheValueProcessor cacheValueProcessor) {
        this.template = template;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
        this.cacheValueProcessor = cacheValueProcessor;
        this.ttlInMillis = ttlInMillis;
    }

    @Override
    public Object getCacheValue(
            String key, CachedMethod cachedMethod) {
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

                try {
                    Object computed = cachedMethod.invoke();
                    String data = serializeData(computed, key, logger);
                    String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data, ttlInMillis);
                    template.opsForValue().set(key, cacheableValue);
                    template.opsForValue().getOperations().delete(lockKey);
                    return computed;
                } catch (CachedMethodInvocationException e) {
                    template.opsForValue().getOperations().delete(lockKey);
                    final Throwable t = e.getCause();
                    if (t instanceof EducationException) {
                        throw (EducationException) t;
                    }
                    return oldData;
                }

            }
            return null;
        }
    }
}

