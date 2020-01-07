package com.paytm.digital.education.metrics.aspects;

import com.paytm.digital.education.metrics.DataDogClient;
import com.paytm.digital.education.metrics.MetricConstant;
import com.paytm.digital.education.metrics.annotations.MethodCounter;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@AllArgsConstructor
public class MethodCounterAspect {

    private DataDogClient metricClient;

    @Around("@annotation(com.paytm.digital.education.metrics.annotations.MethodCounter)")
    public Object incrementCount(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        MethodCounter counter = method.getAnnotation(MethodCounter.class);

        String metricName;
        if (counter.name().isEmpty()) {
            metricName = className + MetricConstant.SEPARATOR + methodName;
        } else {
            metricName = counter.name();
        }

        metricClient.recordRequestRate(metricName, counter.count());

        Long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            if (counter.recordLatency()) {
                Long endTime = System.currentTimeMillis();

                metricClient.recordExecutionTime(metricName + MetricConstant.SEPARATOR
                        + MetricConstant.LATENCY_STR, endTime - startTime);
            }
        }
    }
}
