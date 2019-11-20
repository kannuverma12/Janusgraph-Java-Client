package com.paytm.digital.education.application.config.aspect;

import com.paytm.digital.education.application.config.metric.MetricsAgent;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
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

    @Around("execution(* org.springframework.web.client.RestTemplate.exchange(..)) "
            + "&& args(url,method,requestEntity,..)")
    public <T> Object executeRestExternalMethods(ProceedingJoinPoint proceedingJoinPoint, URI url,
            HttpMethod method, @Nullable
            HttpEntity<T> requestEntity) {
        Map<String, Object> requestMap = new LinkedHashMap<>();
        requestMap.put("ReqType", "Outgoing");
        requestMap.put("Type", "Request");
        requestMap.put("HttpMethod", method);
        requestMap.put("Path", url);
        requestMap.put("ReqEntity", requestEntity);

        Map<String, Object> responseMap = new LinkedHashMap<>();

        Object value = null;
        try {
            value = proceedingJoinPoint.proceed();

            ResponseEntity<T> responseEntity = (ResponseEntity<T>) value;
            responseMap.put("ReqType", "Outgoing");
            responseMap.put("Type", "Response");
            responseMap.put("Path", url);
            responseMap.put("HttpStatus", responseEntity.getStatusCode());
            responseMap.put("ResHeaders", responseEntity.getHeaders());
            responseMap.put("ResBody", responseEntity.getBody());
        } catch (Throwable e) {
            LOGGER.error("Exception occured while logging outgoing request :", e);
        }
        LOGGER.info("Outgoing Request : {} , Outgoing Response : {}",
                JsonUtils.toJson(requestMap), JsonUtils.toJson(responseMap));
        return value;
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
