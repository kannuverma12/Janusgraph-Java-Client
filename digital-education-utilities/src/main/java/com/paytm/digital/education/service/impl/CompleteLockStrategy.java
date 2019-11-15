package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.CacheLockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.paytm.digital.education.utility.SerializationUtils.toHexString;
import static java.time.Duration.ofSeconds;

@Service
@Slf4j
public class CompleteLockStrategy implements CacheLockStrategy {

    private static final int PROCESS_SLEEP_TIME_IN_MILLIS = 500;
    private static final int NUMBER_OF_TIMES_THREAD_RETRIES_BEFORE_GIVING_UP = 10;

    private final RedisTemplate<String, String> template;
    private final int lockDurationForProcessInSeconds;
    private final CacheValueProcessor cacheValueProcessor;


    public CompleteLockStrategy(
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

        long id = Thread.currentThread().getId();
        String lockKey = key + "::zookeeper";

        for (int i = 0; i < 100000; ++i) {
            String processId = RandomStringUtils.randomAlphabetic(10);
            log.info("entered try block " + id + " " + processId);
            log.info("attempting acquire lock for " + id + " " + processId + " " + lockKey);
            Boolean success = template.opsForValue().setIfAbsent(lockKey, processId,
                    ofSeconds(lockDurationForProcessInSeconds));
            if (BooleanUtils.isNotTrue(success)) {
                log.info("failed to acquire lock sleeping " + id + " " + processId);
                sleep(10, key);
                continue;
            }
            log.info("successfully acquired lock " + id + " " + processId);
            T data = null;
            try {
                data = getData.doGetData();
                checkData.doCheckData(data);
                template.opsForValue().getOperations().delete(lockKey);
                log.info("found old value " + id + " " + processId);
                return new Response<>(data, null);
            } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
                log.info("old value won't do " + id + " " + processId + " " + data);
            }

            try {
                log.info("trying to compute " + id + " " + processId);
                U computed = cachedMethod.invoke();
                log.info("computed successfully " + id + " " + processId);
                String serialized = serializeData(computed, key);
                String appended = cacheValueProcessor.appendExpiryDateToValue(serialized, 500000);
                log.info("serialized + appended " + id + " " + processId + " " + appended);
                template.opsForValue().set(key, appended);
                template.opsForValue().getOperations().delete(lockKey);
                log.info("released lock " + id + " " + processId + " " + appended);
                return new Response<>(null, computed);
            } catch (Throwable e) {
                log.error("threw", e);
            }

            log.info("returning null " + id + " " + processId);
            return null;
        }

        log.info("out of loop " + id);
        return null;

    }

    private <U> U writeAndReleaseLock(String lockKey, String processId, CachedMethod<U> cachedMethod,
                                      WriteData<U> writeData) {
        return template.execute(new SessionCallback<U>() {
            @Override
            public <K, V> U execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.watch((K) lockKey);
                if (processId.equals(operations.opsForValue().get(lockKey))) {
                    U u = null;
                    try {
                        u = cachedMethod.invoke();
                        operations.multi();
                        writeData.doWriteData(operations, u);
                        operations.exec();
                    } catch (CachedMethodInvocationException e) {
                        operations.multi();
                        operations.opsForValue().getOperations().delete((K) lockKey);
                        operations.exec();
                        final Throwable t = e.getCause();
                        if (t instanceof EducationException) {
                            throw (EducationException) t;
                        }
                    }
                    return u;
                }
                operations.unwatch();
                return null;
            }
        });
    }

    private static void sleep(long time, String key) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.info("Sleep for key - {} failed", e, key);
        }
    }

    private String serializeData(Object o, String key) {
        try {
            return toHexString(o);
        } catch (IOException e) {
            log.error("Key - {}, Object - {}. Unable to stringify data for key.", e, key, o);
            throw new SerializationException(e);
        }
    }
}
