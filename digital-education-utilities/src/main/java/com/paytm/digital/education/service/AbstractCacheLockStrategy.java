package com.paytm.digital.education.service;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.impl.CacheValueProcessor;
import com.paytm.education.logger.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

import static com.paytm.digital.education.utility.SerializationUtils.fromHexString;
import static com.paytm.digital.education.utility.SerializationUtils.toHexString;

@RequiredArgsConstructor
public abstract class AbstractCacheLockStrategy implements CacheLockStrategy {
    private final Logger logger;
    private final StringRedisTemplate template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int ttlInMillis;

    protected String serializeData(Object o, String key, Logger logger) {
        try {
            return toHexString(o);
        } catch (IOException e) {
            logger.error("Key - {}. Unable to serialize data for key.", e, key);
            throw new SerializationException(e);
        }
    }

    protected Object deSerializeData(String data, String key, Logger logger) {
        try {
            return fromHexString(data);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Key - {}. Unable to serialize data for key.", e, key);
            throw new SerializationException(e);
        }
    }

    protected Object writeToCacheAndReturn(
            Object oldData, String lockKey, String key, CachedMethod cachedMethod, boolean shouldCacheNull) {
        try {
            Object computed = cachedMethod.invoke();
            if (computed != null || shouldCacheNull) {
                String data = serializeData(computed, key, logger);
                String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data, ttlInMillis);
                template.opsForValue().set(key, cacheableValue);
            }
            template.opsForValue().getOperations().delete(lockKey);
            return computed;
        } catch (CachedMethodInvocationException e) {
            template.opsForValue().getOperations().delete(lockKey);
            logger.error("Encountered exception while running cached method", e);
            final Throwable t = e.getCause();
            if (t instanceof EducationException) {
                throw (EducationException) t;
            }
            return oldData;
        }
    }
}
