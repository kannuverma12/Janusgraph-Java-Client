package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.RedisOrchestrator;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.paytm.digital.education.utility.SerializationUtils.fromHexString;
import static com.paytm.digital.education.utility.SerializationUtils.toHexString;

@Service
public class RedisOrchestratorImpl implements RedisOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(RedisOrchestratorImpl.class);

    private final RedisTemplate<String, String> template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int ttl;
    private final WriteLockStrategy writeLockStrategy;

    public RedisOrchestratorImpl(
            RedisTemplate<String, String> template,
            CacheValueProcessor cacheValueProcessor,
            @Value("${redis.cache.ttl.millis}") int ttl,
            WriteLockStrategy writeLockStrategy) {
        this.template = template;
        this.cacheValueProcessor = cacheValueProcessor;
        this.ttl = ttl;
        this.writeLockStrategy = writeLockStrategy;
    }

    @Override
    public Object get(String key, CachedMethod cachedMethod) {
        final CheckData<String> checkData = new CheckData<String>() {
            @Override
            public void doCheckData(String data) throws OldCacheValueExpiredException, OldCacheValueNullException {
                cacheValueProcessor.parseCacheValueAndValidateExpiry(data);
            }
        };

        final GetData<String> getData = new GetData<String>() {
            @Override
            public String doGetData() {
                String fullData = template.opsForValue().get(key);
                return fullData;
            }
        };

        final ComputeAndWrite<Object> computeAndWrite = new ComputeAndWrite<Object>() {
            @Override
            public Object doComputeAndWrite() throws CachedMethodInvocationException {
                Object o = cachedMethod.invoke();
                String data = serializeData(o, key);
                String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data, ttl);
                template.opsForValue().set(key, cacheableValue);
                return o;
            }
        };

        Response<String, Object> response =
                writeLockStrategy.getCacheValue(key, getData, checkData, computeAndWrite);
        if (response.isOldValue()) {
            try {
                return deSerializeData(
                        cacheValueProcessor.parseCacheValueAndValidateExpiry(response.getOldValue()), key);
            } catch (OldCacheValueNullException | OldCacheValueExpiredException e) {
                return null;
            }
        } else {
            return response.getNewValue();
        }
    }

    private Object deSerializeData(String s, String key) {
        try {
            return fromHexString(s);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Key - {}, Data - {}. Cache data parse failed.", e, key, s);
            template.opsForValue().getOperations().delete(key);
            throw new SerializationException(e);
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
