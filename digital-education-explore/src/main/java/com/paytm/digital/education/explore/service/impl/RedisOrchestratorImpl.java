package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.service.RedisOrchestrator;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisOrchestratorImpl implements RedisOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(RedisOrchestratorImpl.class);

    private final RedisTemplate<String, Object> template;

    private static void sleep(long time, String key) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.debug("Sleep for key - {} failed", e, key);
        }
    }

    @Override
    public Object get(String key, ProceedingJoinPoint joinPoint) {
        for (int i = 0; i < 20; i++) {
            String data = (String) template.opsForValue().get(key);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            if (data == null) {
                try {
                    return generateNewDataAndCache(key, methodSignature.getReturnType(), null, joinPoint);
                } catch (CacheUpdateLockedException e) {
                    sleep(500L, key);
                    continue;
                }
            }

            String[] parts = data.split("]\\*\\*", 2);
            String dateTimeString = parts[0].substring(3);
            DateTime date = DateTime.parse(dateTimeString);

            if (date.isAfterNow()) {
                return JsonUtils.fromJson(parts[1], methodSignature.getReturnType());
            }

            try {
                return generateNewDataAndCache(key, methodSignature.getReturnType(), data, joinPoint);
            } catch (CacheUpdateLockedException e) {
                sleep(500L, key);
            }
        }
        return null;
    }

    /* Not a foolproof solution */
    private Object generateNewDataAndCache(
            String key, Class clazz, String oldData, ProceedingJoinPoint joinPoint) throws CacheUpdateLockedException {
        String lockKey = key + "::zookeeper";
        String random = RandomStringUtils.random(10);
        try {
            Boolean success = template.opsForValue().setIfAbsent(lockKey, random, Duration.ofSeconds(20));
            if (BooleanUtils.isNotTrue(success)) {
                throw new CacheUpdateLockedException();
            }
            Object o = joinPoint.proceed();
            int millis = 5 * 60 * 1000;
            String newDataString = "**[" + new LocalDateTime().plusMillis(millis).toString() + "]**"
                    + JsonUtils.toJson(o);
            writeAndReleaseLock(key, newDataString, lockKey, random);
            return o;
        } catch (CacheUpdateLockedException e) {
            throw e;
        } catch (Throwable e) {
            releaseLock(lockKey, random);
            if (oldData == null) {
                return null;
            }
            return JsonUtils.fromJson(oldData, clazz);
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

    private static class CacheUpdateLockedException extends RuntimeException {
    }
}
