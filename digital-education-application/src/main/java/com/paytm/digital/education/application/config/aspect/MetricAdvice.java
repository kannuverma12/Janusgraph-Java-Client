package com.paytm.digital.education.application.config.aspect;

import com.paytm.digital.education.metrics.DataDogClient;
import com.paytm.digital.education.application.constant.Constant;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Aspect
@Component
public class MetricAdvice {

    private static Logger log = LoggerFactory.getLogger(MetricAdvice.class);

    private static Map<String, AtomicInteger> requestRateMap = new ConcurrentHashMap<>();

    @Autowired
    private DataDogClient dataDogClient;

    /**
     * Number of requests being served at a given point of time.
     */
    @Around("execution( * " + Constant.EDUCATION_BASE_PACKAGE + ".*..*.*(..)) && @annotation(requestRate)")
    public Object recordReqRateAround(ProceedingJoinPoint joinPoint, RequestRate requestRate)
            throws Throwable {
        String metricName = requestRate.metricName();
        if (StringUtils.isBlank(metricName)) {
            return joinPoint.proceed();
        }
        return recordRequestCount(joinPoint, metricName);
    }


    @Around(value = "execution(* " + Constant.APPLICATION_BASE_PACKAGE
            + ".config.persona.PersonaRestClientWrapper.do*Call(..))"
            + " && @annotation(RequestRate) && args(dependencyName,..)")
    public Object recordReqRatePersona(ProceedingJoinPoint joinPoint, String dependencyName)
            throws Throwable {
        log.debug("DependencyName captured as :" + dependencyName);
        if (StringUtils.isBlank(dependencyName)) {
            return joinPoint.proceed();
        }
        return recordRequestCount(joinPoint, dependencyName);
    }


    private Object recordRequestCount(ProceedingJoinPoint joinPoint, String dependencyName) throws Throwable {
        requestRateMap
                .computeIfAbsent(dependencyName, requestCount -> new AtomicInteger())
                .incrementAndGet();
        try {
            return joinPoint.proceed();
        } finally {
            dataDogClient.recordRequestRate(dependencyName, requestRateMap.get(dependencyName).get());
            requestRateMap.get(dependencyName).decrementAndGet();
        }
    }
}
