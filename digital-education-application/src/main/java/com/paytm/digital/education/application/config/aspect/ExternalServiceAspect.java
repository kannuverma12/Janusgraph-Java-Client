package com.paytm.digital.education.application.config.aspect;

import com.paytm.digital.education.application.config.metric.MetricsAgent;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class ExternalServiceAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalServiceAspect.class);

    @Autowired
    private MetricsAgent metricsAgent;

    @Pointcut("execution(* com.paytm.digital.education.database.repository..**(..))")
    public void executeRepositoryMethods() {
    }

    @Pointcut("execution(* com.paytm.digital.education.elasticsearch.service.impl..**(..))")
    public void executeElasticSearchServiceImplMethods() {
    }

    @Pointcut("execution(* com.paytm.digital.education.cache.redis..**(..))")
    public void executeRedisMethods() {
    }

    @Around("executeRepositoryMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        Object output = null;
        long timeStartInMillisecs = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String aspect =
                "mongo_repository." + StringUtils
                        .substringAfterLast(signature.getDeclaringTypeName(), ".") + "." + signature
                        .getName();
        output = recordAspectData(pjp, aspect, timeStartInMillisecs);
        return output;

    }

    @Around("executeElasticSearchServiceImplMethods()")
    public Object daoImplMethods(ProceedingJoinPoint pjp) throws Throwable {
        Object output = null;
        long timeStartInMillisecs = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String aspect =
                "elasticsearch_api." + StringUtils
                        .substringAfterLast(signature.getDeclaringTypeName(), ".") + "." + signature
                        .getName();
        output = recordAspectData(pjp, aspect, timeStartInMillisecs);
        return output;
    }

    @Around("executeRedisMethods()")
    public Object redisMethods(ProceedingJoinPoint pjp) throws Throwable {
        Object output = null;
        long timeStartInMillisecs = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String aspect =
                "redis_api." + StringUtils.substringAfterLast(signature.getDeclaringTypeName(), ".")
                        + "." + signature.getName();
        output = recordAspectData(pjp, aspect, timeStartInMillisecs);
        return output;
    }

    private Object recordAspectData(ProceedingJoinPoint pjp, String aspect,
            long timeStartInMillisecs) throws Throwable {
        Object output = null;
        String methodCaller = getSuperCallerMethodName(pjp);
        try {
            output = pjp.proceed();
        } catch (Throwable t) {
            metricsAgent.incrementfnErrorCount(aspect, t.getClass().getSimpleName(), methodCaller);
            throw t;
        } finally {
            long timeTaken = System.currentTimeMillis() - timeStartInMillisecs;
            metricsAgent.incrementFnCount(aspect, methodCaller);
            metricsAgent.recordExecutionTimeOfFn(aspect, timeTaken, methodCaller);
        }
        return output;
    }

    private String getSuperCallerMethodName(ProceedingJoinPoint pjp) {
        StackTraceElement stackTraceElement = null;
        Integer index = 0;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (; index < stackTraceElements.length; index++) {
            if (stackTraceElements[index].getClassName()
                    .contains(pjp.getSourceLocation().getWithinType().getSimpleName())) {
                stackTraceElement = stackTraceElements[++index];
                break;
            }
        }
        String methodCaller = Optional.ofNullable(stackTraceElement)
                .map(ste ->
                        StringUtils.substringAfterLast(ste.getClassName(), ".") + "." + ste
                                .getMethodName())
                .orElse("unknown");
        return methodCaller;
    }
}
