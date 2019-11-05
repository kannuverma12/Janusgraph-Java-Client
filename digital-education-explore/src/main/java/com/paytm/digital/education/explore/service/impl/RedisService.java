package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.utility.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate< String, Object> template;

    public Object get(String key, ProceedingJoinPoint joinPoint) {
        boolean shouldRetry = false;
        do {
            String data = (String) template.opsForValue().get(key);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            if (data == null) {
                try {
                    return generateNewDataAndCache(key, methodSignature.getReturnType(), null, joinPoint);
                } catch (KeyLockedException e) {
                    Thread.sleep(500L);
                    shouldRetry = true;
                } catch (InterruptedException e) {

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
                shouldRetry = true;
            }
        } while (shouldRetry);
    }

    private Object generateNewDataAndCache(String key, Class clazz, String oldData, ProceedingJoinPoint joinPoint) throws KeyLockedException{
        try {
            Object o = joinPoint.proceed();
            int millis = 5 * 60 * 1000;
            String newDataString = "**[" + new LocalDateTime().plusMillis(millis).toString() + "]**" + JsonUtils.toJson(o);
            template.opsForValue().set(key, newDataString);
            return o;
        } catch (Throwable e) {
            if (oldData == null) return null;
            return JsonUtils.fromJson(oldData, clazz);
        }
    }

    private static class KeyLockedException extends RuntimeException {}
}
