package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.CacheLockStrategy;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.paytm.digital.education.utility.SerializationUtils.fromHexString;
import static com.paytm.digital.education.utility.SerializationUtils.toHexString;
import static java.time.Duration.ofSeconds;

@Service
public class WriteLockStrategy implements CacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);

    private final RedisTemplate<String, String> template;
    private final int lockDurationForProcessInSeconds;
    private final CacheValueProcessor cacheValueProcessor;
    private final int ttlInMillis;

    public WriteLockStrategy(
            RedisTemplate<String, String> template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            @Value("${redis.cache.ttl.millis}") int ttlInMillis,
            CacheValueProcessor cacheValueProcessor) {
        this.template = template;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
        this.cacheValueProcessor = cacheValueProcessor;
        this.ttlInMillis = ttlInMillis;
    }

    @Override
    /** TODOs:-
     * 1. Replace intern with concurrent maps.
     *    See:- https://stackoverflow.com/questions/133988/synchronizing-on-string-objects-in-java#answer-134154
     * 2. Add lock token in redis so that processes don't remove other processes' locks
     */
    public Object getCacheValue(
            String key, CachedMethod cachedMethod) {
        CacheData cacheData = cacheValueProcessor.parseCacheValue(template.opsForValue().get(key));
        Object oldData = null;
        if (cacheData.getExpiryDateTime() != null && cacheData.getData() != null) {
            oldData = deSerializeData(cacheData.getData(), key);
        }

        if (cacheData.getExpiryDateTime() != null && cacheData.getExpiryDateTime().isAfterNow()) {
            return oldData;
        }

        logger.debug("Old cache value for key ", key);

        synchronized (key.intern()) {
            String lockKey = key + "::redisLock";
            Boolean success = template.opsForValue()
                    .setIfAbsent(lockKey, "1", ofSeconds(lockDurationForProcessInSeconds));
            if (BooleanUtils.isNotTrue(success)) {
                return oldData;
            }
            try {
                Object computed = cachedMethod.invoke();
                String data = serializeData(computed, key);
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
    }

    private String serializeData(Object o, String key) {
        try {
            return toHexString(o);
        } catch (IOException e) {
            logger.error("Key - {}, Object - {}. Unable to stringify data for key.", e, key, o);
            throw new SerializationException(e);
        }
    }

    private Object deSerializeData(String data, String key) {
        try {
            return fromHexString(data);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Key - {}, Object - {}. Unable to stringify data for key.", e, key, data);
            throw new SerializationException(e);
        }
    }
}
