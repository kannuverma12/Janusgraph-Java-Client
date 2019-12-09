package com.paytm.digital.education.advice;

import com.paytm.digital.education.advice.helper.KeyGenerator;
import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.method.MethodEnclosedInProceedingJoinPoint;
import com.paytm.digital.education.service.RedisOrchestrator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheAdvice {

    private final RedisOrchestrator redisOrchestrator;
    private final KeyGenerator keyGenerator;

    @Around("@annotation(com.paytm.digital.education.annotation.EduCache)")
    public Object interceptCachedMethodCalls(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        Method method = signature.getMethod();
        EduCache eduCacheAnnotation = method.getAnnotation(EduCache.class);
        String cacheKey = keyGenerator.generateKey(
                eduCacheAnnotation, method.getDeclaringClass(), method.getName(), parameterNames, args);
        return redisOrchestrator.get(cacheKey, new MethodEnclosedInProceedingJoinPoint(pjp));
    }
}
