package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
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

import static com.paytm.digital.education.utility.SerializationUtils.toHexString;
import static java.time.Duration.ofSeconds;

@Service
public class WriteLockStrategy implements CacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);

    private final RedisTemplate<String, String> template;
    private final int lockDurationForProcessInSeconds;
    private final CacheValueProcessor cacheValueProcessor;

    public WriteLockStrategy(
            RedisTemplate<String, String> template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            CacheValueProcessor cacheValueProcessor) {
        this.template = template;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
        this.cacheValueProcessor = cacheValueProcessor;
    }

    @Override
    /** TODOs:-
     * 1. Replace intern with concurrent maps.
     *    See:- https://stackoverflow.com/questions/133988/synchronizing-on-string-objects-in-java#answer-134154
     * 2. Add lock token in redis so that processes don't remove other processes' locks
     */
    public <T, U> Response<T, U> getCacheValue(
            String key, GetData<T> getData, CheckData<T> checkData,
            WriteData<U> writeData, CachedMethod<U> cachedMethod) {
        T oldData = null;
        try {
            oldData = getData.doGetData();
            checkData.doCheckData(oldData);
            return new Response<>(oldData, null);
        } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
            logger.debug("Old cache value exception", e);
        }

        synchronized (key.intern()) {
            String lockKey = key + "::redisLock";
            Boolean success = template.opsForValue()
                    .setIfAbsent(lockKey, "1", ofSeconds(lockDurationForProcessInSeconds));
            if (BooleanUtils.isNotTrue(success)) {
                return new Response<>(oldData, null);
            }
            try {
                U u = cachedMethod.invoke();
                String data = serializeData(u, key);
                String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data, 3600000);
                template.opsForValue().set(key, cacheableValue);
                template.opsForValue().getOperations().delete(lockKey);
                return new Response<>(null, u);
            } catch (CachedMethodInvocationException e) {
                template.opsForValue().getOperations().delete(lockKey);
                final Throwable t = e.getCause();
                if (t instanceof EducationException) {
                    throw (EducationException) t;
                }
                return new Response<>(oldData, null);
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
}
