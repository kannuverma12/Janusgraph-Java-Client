package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.CacheLockStrategy;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.paytm.digital.education.utility.SerializationUtils.toHexString;

@Service
public class CompleteLockStrategy implements CacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);

    private final RedisTemplate<String, String> template;
    private final CacheValueProcessor cacheValueProcessor;
    private final ConcurrentMap<String, Object> lockTable = new ConcurrentHashMap<>();


    public CompleteLockStrategy(
            RedisTemplate<String, String> template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            CacheValueProcessor cacheValueProcessor) {
        this.template = template;
        this.cacheValueProcessor = cacheValueProcessor;
    }

    @Override
    public Object getCacheValue(
            String key, CachedMethod cachedMethod) {
        synchronized (key.intern()) {
            CacheData cacheData = cacheValueProcessor.parseCacheValue(template.opsForValue().get(key));
            Object oldData = null;
            if (cacheData.getExpiryDateTime() != null && cacheData.getData() != null) {
                oldData = deSerializeData(cacheData.getData(), key);
            }

            if (cacheData.getExpiryDateTime() != null && cacheData.getExpiryDateTime().isAfterNow()) {
                return oldData;
            }

            logger.debug("Old cache value for key ", key);

            try {
                Object computed = cachedMethod.invoke();
                String data = serializeData(computed, key);
                String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data, 3600000);
                return computed;
            } catch (CachedMethodInvocationException e) {
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

    private String deSerializeData(Object o, String key) {
        try {
            return toHexString(o);
        } catch (IOException e) {
            logger.error("Key - {}, Object - {}. Unable to stringify data for key.", e, key, o);
            throw new SerializationException(e);
        }
    }
}

