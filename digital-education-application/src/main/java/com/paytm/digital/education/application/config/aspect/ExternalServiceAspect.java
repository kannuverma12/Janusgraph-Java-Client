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
    public void executeMongoMethods() {
    }

    @Pointcut("execution(* com.paytm.digital.education.elasticsearch.service.impl..**(..))")
    public void executeElasticSearchServiceImplMethods() {
    }

    @Pointcut("execution(* com.paytm.digital.education.cache.redis..**(..))")
    public void executeRedisMethods() {
    }

    @Around("executeMongoMethods()")
    public Object mongoMethods(ProceedingJoinPoint pjp) throws Throwable {
        return execute(pjp, "mongo_api" );
    }

    @Around("executeElasticSearchServiceImplMethods()")
    public Object elasticSearchMethods(ProceedingJoinPoint pjp) throws Throwable {
        return execute(pjp, "elasticsearch_api" );
    }

    @Around("executeRedisMethods()")
    public Object redisMethods(ProceedingJoinPoint pjp) throws Throwable {
        return execute(pjp, "redis_api" );
    }

    private Object execute(ProceedingJoinPoint pjp, String aspectPrefix) throws Throwable {
        Object output = null;
        long timeStartInMillisecs = System.currentTimeMillis();
        String aspect = getMetricName(pjp, aspectPrefix);
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

    private String getMetricName(ProceedingJoinPoint pjp, String prefix) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return new StringBuilder(prefix).append(".").append(StringUtils
                .substringAfterLast(signature.getDeclaringTypeName(), "."))
                .append(signature.getName()).toString();
    }
}
