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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.paytm.digital.education.utility.SerializationUtils.toHexString;
import static java.time.Duration.ofSeconds;

@Service
public class ReferenceStrategy implements CacheLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);
    private static final int PROCESS_SLEEP_TIME_IN_MILLIS = 500;
    private static final int NUMBER_OF_TIMES_THREAD_RETRIES_BEFORE_GIVING_UP = 10;

    private final RedisTemplate<String, String> template;
    private final int lockDurationForProcessInSeconds;
    private final CacheValueProcessor cacheValueProcessor;
    private final ConcurrentMap<String, Object> lockTable = new ConcurrentHashMap<>();


    public ReferenceStrategy(
            RedisTemplate<String, String> template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            CacheValueProcessor cacheValueProcessor) {
        this.template = template;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
        this.cacheValueProcessor = cacheValueProcessor;
    }

    @Override
    public <T, U> Response<T, U> getCacheValue(
            String key, GetData<T> getData, CheckData<T> checkData,
            WriteData<U> writeData, CachedMethod<U> cachedMethod) {
        synchronized (key.intern()) {
            T data = null;
            try {
                data = getData.doGetData();
                checkData.doCheckData(data);
                return new Response<>(data, null);
            } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
                logger.debug("Old cache value exception", e);
            }

            try {
                U u = cachedMethod.invoke();
                String data2 = serializeData(u, key);
                String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data2, 3600000);
                template.opsForValue().set(key, cacheableValue);
                return new Response<>(null, u);
            } catch (CachedMethodInvocationException e) {
                final Throwable t = e.getCause();
                if (t instanceof EducationException) {
                    throw (EducationException) t;
                }
                return new Response<>(data, null);
            }
        }
        //        Object lock = lockTable.get(key);
        //        if (lock == null) {
        //
        //        } else {
        //            synchronized (lock) {}
        //        }
        //        if (lockTable.putIfAbsent(key, 1) != null) {
        //            String lockKey = key + "::zookeeper";
        //            T data = null;
        //            try {
        //                data = getData.doGetData();
        //                checkData.doCheckData(data);
        //                return new Response<>(data, null);
        //            } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
        //                logger.debug("Old cache value exception", e);
        //            }
        //
        //
        //        }
        //        synchronized (key) {
        //
        //        }
        //        String lockKey = key + "::zookeeper";
        //        T data = null;
        //        try {
        //            data = (T) CacheClass.value;
        //            checkData.doCheckData(data);
        //            return new Response<>(data, null);
        //        } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
        //            logger.debug("Old cache value exception", e);
        //        }
        //
        //        writeData();
        //
        //        String processId = RandomStringUtils.randomAlphabetic(10);
        //        if (mutex.getAndSet(1) == 1) {
        //            return new Response<>(data, null);
        //        }
        //        //        Boolean success = template.opsForValue().setIfAbsent(lockKey, processId,
        //        //                ofSeconds(lockDurationForProcessInSeconds));
        //        //        if (BooleanUtils.isNotTrue(success)) {
        //        //            return new Response<>(data, null);
        //        //        }
        //
        //        try {
        //            U u = cachedMethod.invoke();
        //            String data2 = serializeData(u, key);
        //            String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(data2, 3600000);
        //            CacheClass.value = cacheableValue;
        //            // template.opsForValue().set(key, cacheableValue);
        //            // template.opsForValue().getOperations().delete(lockKey);
        //            mutex.set(0);
        //            return new Response<>(null, u);
        //        } catch (CachedMethodInvocationException e) {
        //            final Throwable t = e.getCause();
        //            if (t instanceof EducationException) {
        //                throw (EducationException) t;
        //            }
        //            return new Response<>(data, null);
        //        }
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

