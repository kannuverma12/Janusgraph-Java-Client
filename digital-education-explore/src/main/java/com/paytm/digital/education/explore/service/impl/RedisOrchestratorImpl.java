package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.CacheUpdateLockedException;
import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import com.paytm.digital.education.exception.SerializationException;
import com.paytm.digital.education.explore.service.CachedMethod;
import com.paytm.digital.education.explore.service.RedisOrchestrator;
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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

import static com.paytm.digital.education.utility.SerializationUtils.fromHexString;
import static com.paytm.digital.education.utility.SerializationUtils.toHexString;
import static java.time.Duration.ofSeconds;

@Service
public class RedisOrchestratorImpl implements RedisOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(RedisOrchestratorImpl.class);
    private static final int PROCESS_SLEEP_TIME_IN_MILLIS = 500;
    private static final int NUMBER_OF_TIMES_THREAD_RETRIES_BEFORE_GIVING_UP = 10;

    private final RedisTemplate<String, Object> template;
    private final CacheValueProcessor cacheValueProcessor;
    private final int ttl;
    private final int lockDurationForProcessInSeconds;

    public RedisOrchestratorImpl(
            RedisTemplate<String, Object> template,
            CacheValueProcessor cacheValueProcessor,
            @Value("${redis.cache.process.lock.duration.seconds}") int lockDurationForProcessInSeconds,
            @Value("${redis.cache.ttl.millis}") int ttl) {
        this.template = template;
        this.cacheValueProcessor = cacheValueProcessor;
        this.ttl = ttl;
        this.lockDurationForProcessInSeconds = lockDurationForProcessInSeconds;
    }

    @Override
    public Object get(String key, CachedMethod cachedMethod) {
        for (int i = 0; i < NUMBER_OF_TIMES_THREAD_RETRIES_BEFORE_GIVING_UP; ++i) {
            String data = (String) template.opsForValue().get(key);
            try {
                String oldData = cacheValueProcessor.parseCacheValueAndValidateExpiry(data);
                return deSerializeData(oldData, key);
            } catch (OldCacheValueExpiredException | OldCacheValueNullException e) {
                logger.debug("Old cache value exception", e);
            }

            try {
                return generateNewDataAndCache(key, cachedMethod, data);
            } catch (CacheUpdateLockedException cacheUpdateLockedException) {
                sleep(PROCESS_SLEEP_TIME_IN_MILLIS, key);
            }
        }
        return null;
    }

    /* Not a foolproof solution */
    private Object generateNewDataAndCache(
            String key, CachedMethod cachedMethod, String oldData) throws CacheUpdateLockedException {
        String lockKey = key + "::zookeeper";
        String random = RandomStringUtils.randomAlphabetic(10);
        try {
            Boolean success = template.opsForValue().setIfAbsent(lockKey, random,
                    ofSeconds(lockDurationForProcessInSeconds));
            if (BooleanUtils.isNotTrue(success)) {
                throw new CacheUpdateLockedException();
            }
            Object o = cachedMethod.invoke();
            String cacheableValue = cacheValueProcessor.appendExpiryDateToValue(serializeData(o, key), ttl);
            writeAndReleaseLock(key, cacheableValue, lockKey, random);
            return o;
        } catch (CachedMethodInvocationException e) {
            return handleMethodInvocationException(e, key, oldData, lockKey, random);
        }
    }

    private Object handleMethodInvocationException(
            CachedMethodInvocationException e, String key, String oldData, String lockKey, String random) {
        releaseLock(lockKey, random);
        Throwable t = e.getCause();
        if (t instanceof EducationException) {
            throw (EducationException) t;
        }
        if (oldData == null) {
            return null;
        }
        return deSerializeData(oldData, key);
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

    private void writeAndReleaseLock(String key, String value, String lockKey, String identifier) {
        template.execute(new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.watch((K) lockKey);
                if (identifier.equals(operations.opsForValue().get(lockKey))) {
                    operations.multi();
                    operations.opsForValue().set((K) key, (V) value);
                    operations.opsForValue().getOperations().delete((K) lockKey);
                    List<Object> result = operations.exec();
                    if (!CollectionUtils.isEmpty(result)) {
                        return true;
                    }
                }
                operations.unwatch();
                return false;
            }
        });
    }

    private void releaseLock(String lockKey, String identifier) {
        template.execute(new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.watch((K) lockKey);
                if (identifier.equals(operations.opsForValue().get(lockKey))) {
                    operations.multi();
                    operations.opsForValue().getOperations().delete((K) lockKey);
                    List<Object> result = operations.exec();
                    if (!CollectionUtils.isEmpty(result)) {
                        return true;
                    }
                }
                operations.unwatch();
                return false;
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
