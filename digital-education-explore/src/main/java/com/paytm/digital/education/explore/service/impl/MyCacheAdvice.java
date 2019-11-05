package com.paytm.digital.education.explore.service.impl;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
public class MyCacheAdvice {
    private static final Logger log = LoggerFactory.getLogger(MyCacheAdvice.class);

    private final RedisService redisService;

    @Around("@annotation(MyCache)")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, parameterNames.length).boxed().forEach(i -> params.put(parameterNames[i], args[i]));

        Method method = signature.getMethod();

        MyCache myAnnotation = method.getAnnotation(MyCache.class);
        String[] keys = myAnnotation.keys();
        String finalKey = "";

        for (String key : keys) {
            String[] parts = key.split("\\.");
            Object val = params.get(parts[0]);
            if (parts.length > 1) {
                PropertyUtilsBean pub = new PropertyUtilsBean();
                String kk = String.join(".", Arrays.copyOf(parts, 1));
                val = pub.getProperty(val, kk);
            }
            finalKey += val;

        }

        return redisService.get(finalKey, joinPoint);
    }
}
