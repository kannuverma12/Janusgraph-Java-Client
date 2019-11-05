package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.utility.JsonUtils;
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
public class RedisService {
    private final RedisTemplate<String, Object> template;

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public Object get(String key, ProceedingJoinPoint joinPoint) {
        for (int i = 0; i < 10; i++) {
            String data = (String) template.opsForValue().get(key);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            if (data == null) {
                try {
                    return generateNewDataAndCache(key, methodSignature.getReturnType(), null, joinPoint);
                } catch (KeyLockedException e) {
                    sleep(500L);
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
                return generateNewDataAndCache(key, methodSignature.getReturnType(), null, joinPoint);
            } catch (KeyLockedException e) {
                sleep(500L);
            }
        }
        return null;
    }

    private Object generateNewDataAndCache(String key, Class clazz, String oldData, ProceedingJoinPoint joinPoint) throws KeyLockedException {
        String lockKey = key + "::zookeeper";
        String random = RandomStringUtils.random(10);
        try {
            Boolean success = template.opsForValue().setIfAbsent(lockKey, random, Duration.ofMillis(5000));
            if (BooleanUtils.isNotTrue(success)) throw new KeyLockedException();
            Object o = joinPoint.proceed();
            int millis = 5 * 60 * 1000;
            String newDataString = "**[" + new LocalDateTime().plusMillis(millis).toString() + "]**" + JsonUtils.toJson(o);
            template.opsForValue().set(key, newDataString);
            relinquishLock(lockKey, random);
            return o;
        } catch (KeyLockedException e) {
            throw e;
        } catch (Throwable e) {
            relinquishLock(lockKey, random);
            if (oldData == null) return null;
            return JsonUtils.fromJson(oldData, clazz);
        }
    }

    private void relinquishLock(String lockKey, String ownRandomValue) {
        String currentRandomValue = (String) template.opsForValue().get(lockKey);
        if (ownRandomValue.equals(currentRandomValue)) {
            template.opsForValue().getOperations().delete(lockKey);
        }
    }

    private static class KeyLockedException extends RuntimeException {
    }
}
