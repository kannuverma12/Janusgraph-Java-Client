package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import com.paytm.digital.education.method.CachedMethod;
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

import static java.time.Duration.ofSeconds;

@Service
public class WriteLockStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WriteLockStrategy.class);
    private static final int PROCESS_SLEEP_TIME_IN_MILLIS = 500;
    private static final int NUMBER_OF_TIMES_THREAD_RETRIES_BEFORE_GIVING_UP = 10;

    private final RedisTemplate<String, String> template;
    private final int lockDurationForProcessInSeconds;


    public WriteLockStrategy(
            RedisTemplate<String, String> template,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds) {
        this.template = template;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
    }

    public <T, U> Response<T, U> getCacheValue(
            String key, GetData<T> getData, CheckData<T> checkData,
            WriteData<U> writeData, CachedMethod<U> cachedMethod) {
        Long id = Thread.currentThread().getId();
        for (int i = 0; i < NUMBER_OF_TIMES_THREAD_RETRIES_BEFORE_GIVING_UP; ++i) {
            T data = null;
            try {
                logger.info("checking " + id);
                data = getData.doGetData();
                checkData.doCheckData(data);
                logger.info("old value is sufficient " + id + " " + data);
                return new Response<>(data, null);
            } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
                logger.debug("Old cache value exception", e);
            }

            logger.info("trying, generating " + id);
            String lockKey = key + "::zookeeper";
            String processId = RandomStringUtils.randomAlphabetic(10);

            logger.info("generated " + id);
            Boolean success = template.opsForValue().setIfAbsent(lockKey, processId,
                    ofSeconds(lockDurationForProcessInSeconds));
            if (BooleanUtils.isNotTrue(success)) {
                logger.info("failed " + id);
                sleep(PROCESS_SLEEP_TIME_IN_MILLIS, key);
                continue;
            }

            logger.info("success " + id);
            U computed = writeAndReleaseLock(lockKey, processId, cachedMethod, writeData);
            return new Response<>(data, computed);
        }

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
                    } catch (CachedMethodInvocationException e) {
                        Throwable t = e.getCause();
                        if (t instanceof EducationException) {
                            operations.multi();
                            operations.opsForValue().getOperations().delete((K) lockKey);
                            operations.exec();
                            throw (EducationException) t;
                        }
                    } finally {
                        operations.multi();
                        writeData.doWriteData(operations, u);
                        operations.opsForValue().getOperations().delete((K) lockKey);
                        operations.exec();
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
            logger.debug("Sleep for key - {} failed", e, key);
        }
    }
}
