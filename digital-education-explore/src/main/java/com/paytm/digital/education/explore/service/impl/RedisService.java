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
        String data = (String) template.opsForValue().get(key);
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        if (data == null) {
            return generateNewDataAndCache(key, methodSignature.getReturnType(), null, joinPoint);
        }

        String[] parts = data.split("]\\*\\*", 2);
        String dateTimeString = parts[0].substring(3);
        DateTime date = DateTime.parse(dateTimeString);

        if (date.isAfterNow()) {
            return JsonUtils.fromJson(parts[1], methodSignature.getReturnType());
        }

        return generateNewDataAndCache(key, methodSignature.getReturnType(), parts[1], joinPoint);
    }

    private Object generateNewDataAndCache(String key, Class clazz, String oldData, ProceedingJoinPoint joinPoint) {
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
}
