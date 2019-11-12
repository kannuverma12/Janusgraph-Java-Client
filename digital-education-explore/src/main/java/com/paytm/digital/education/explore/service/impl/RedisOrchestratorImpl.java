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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

@Service
@RequiredArgsConstructor
public class RedisOrchestratorImpl implements RedisOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(RedisOrchestratorImpl.class);
    private static final int LOCK_DURATION_FOR_PROCESS_IN_SECONDS = 10;
    private static final int NUMBER_OF_BACKLOG_PROCESSES = 3;
    private static final int PROCESS_SLEEP_TIME_IN_MILLIS = 1000;
    private static final int TTL = 5 * 60 * 1000;

    private final RedisTemplate<String, Object> template;
    private final CacheValueProcessor cacheValueProcessor;

    @Override
    public Object get(String key, CachedMethod cachedMethod) {
        int times = (LOCK_DURATION_FOR_PROCESS_IN_SECONDS * 1000 * NUMBER_OF_BACKLOG_PROCESSES)
                / PROCESS_SLEEP_TIME_IN_MILLIS;
        for (int i = 0; i < times; i++) {
            String data = (String) template.opsForValue().get(key);
            try {
                String oldData = cacheValueProcessor.fromCacheValueFormatIfValid(data);
                return fromString(oldData);
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
        String random = RandomStringUtils.random(10);
        try {
            Boolean success = template.opsForValue().setIfAbsent(lockKey, random, Duration.ofSeconds(20));
            if (BooleanUtils.isNotTrue(success)) {
                throw new CacheUpdateLockedException();
            }
            Object o = cachedMethod.invoke();
            String cacheableValue = cacheValueProcessor.toCacheValueFormat(toString(o), TTL);
            writeAndReleaseLock(key, cacheableValue, lockKey, random);
            return o;
        } catch (CachedMethodInvocationException e) {
            return handleMethodInvocationException(e, oldData, lockKey, random);
        }
    }

    private Object handleMethodInvocationException(
            CachedMethodInvocationException e, String oldData, String lockKey, String random) {
        Throwable t = e.getCause();
        if (t instanceof EducationException) {
            throw (EducationException) t;
        }
        releaseLock(lockKey, random);
        if (oldData == null) {
            return null;
        }
        return fromString(oldData);
    }

    private Object fromString(String s) {
        try {
            byte[] data = parseHexBinary(s);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("broke", e);
            throw new SerializationException(e);
        }
    }

    private String toString(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            return printHexBinary(baos.toByteArray());
        } catch (IOException e) {
            logger.error("broke", e);
            throw new SerializationException(e);
        }
    }

    private void writeAndReleaseLock(String key, String value, String lockKey, String identifier) {
        String currentIdentifier = (String) template.opsForValue().get(lockKey);
        if (identifier.equals(currentIdentifier)) {
            template.opsForValue().set(key, value);
            template.opsForValue().getOperations().delete(lockKey);
        }
    }

    private void releaseLock(String lockKey, String ownRandomValue) {
        String currentRandomValue = (String) template.opsForValue().get(lockKey);
        if (ownRandomValue.equals(currentRandomValue)) {
            template.opsForValue().getOperations().delete(lockKey);
        }
    }

    private static void sleep(long time, String key) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.debug("Sleep for key - {} failed", e, key);
        }
    }
}
