package com.paytm.digital.education.application.config.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paytm.digital.education.application.config.metric.DataDogClient;
import com.paytm.digital.education.application.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class TimedAdvice {

    private static final String EXECUTION_TIME = "ExecutionTime(ms)";
    private static final String METHOD_NAME    = "MethodName";
    private static final String CLASS_NAME     = "ClassName";
    private static final String LOG_TO_DATADOG = "LogToDatadog";
    private static final String BRACES         = "{}";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DataDogClient dataDogClient;

    @Around("execution( * " + Constant.EDUCATION_BASE_PACKAGE + ".*..*.*(..)) && @annotation(timedLog)")
    public Object logAround(ProceedingJoinPoint joinPoint, Timed timedLog) throws Throwable {
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            logTime(joinPoint, timedLog.logToDataDog(), timedLog.metricName(), startTime);
        }
    }

    @Around(value = "execution(* " + Constant.APPLICATION_BASE_PACKAGE
            + ".config.persona.PersonaRestClientWrapper.do*Call(..)) "
            + "&& @annotation(timedLog) && args(dependencyName,..)")
    public Object logAroundPersona(ProceedingJoinPoint joinPoint, Timed timedLog,
            String dependencyName) throws Throwable {
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            logTime(joinPoint, timedLog.logToDataDog(), dependencyName, startTime);
        }
    }

    private void logTime(ProceedingJoinPoint joinPoint, boolean logToDataDaog,
            String dependencyName, long startTime) throws JsonProcessingException {
        ObjectNode logObject = mapper.createObjectNode();

        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        logObject.put(METHOD_NAME, methodName);

        String className = joinPoint.getSignature().getDeclaringTypeName();
        logObject.put(CLASS_NAME, className);

        long elapsedTime = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

        logObject.put(EXECUTION_TIME, elapsedTime);
        logObject.put(LOG_TO_DATADOG, logToDataDaog);

        if (logToDataDaog && !StringUtils.isBlank(dependencyName)) {
            dataDogClient.recordExecutionTime(dependencyName, elapsedTime);
        }
        log.info(BRACES, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logObject));
    }
}
