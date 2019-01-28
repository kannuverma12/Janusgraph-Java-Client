package com.paytm.digital.education.application.config.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paytm.digital.education.application.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAdvice {

    private static final int    OUTPUT_STRING_SIZE_LIMIT = 10240;
    private static final String INPUT                    = "input";
    private static final String EXCEPTION                = "exception";
    private static final String OUTPUT                   = "output";
    private static final String SERVICE_NAME             = "ServiceName";
    private static final String METHOD_NAME              = "MethodName";
    private static final String CLASS_NAME               = "ClassName";
    private static final String BRACES                   = "{}";

    @Autowired
    private ObjectMapper mapper;

    @Around("execution( * " + Constant.EDUCATION_BASE_PACKAGE + ".*..*.*(..)) && @annotation(logReqResp)")
    public Object logAround(ProceedingJoinPoint joinPoint, LogRequestResponse logReqResp) throws Throwable {
        String serviceName = logReqResp.serviceName();
        if (StringUtils.isBlank(serviceName)) {
            return joinPoint.proceed();
        }
        return logRequestResponse(joinPoint, serviceName, logReqResp.logOnInfoMode());
    }

    @Around(value = "execution(* " + Constant.APPLICATION_BASE_PACKAGE
            + ".config.persona.PersonaRestClientWrapper.do*Call(..)) "
            + " && @annotation(logReqResp) && args(dependencyName,..)")
    public Object logAroundPersona(ProceedingJoinPoint joinPoint, LogRequestResponse logReqResp, String dependencyName)
            throws Throwable {
        log.debug("DependencyName captured as :" + dependencyName);
        if (StringUtils.isBlank(dependencyName)) {
            return joinPoint.proceed();
        }
        return logRequestResponse(joinPoint, dependencyName, logReqResp.logOnInfoMode());
    }

    private Object logRequestResponse(ProceedingJoinPoint joinPoint, String serviceName, boolean logOnInfoMode)
                    throws Throwable, JsonProcessingException {
        ObjectNode requestLog = processInput(joinPoint, serviceName);
        try {
            Object output = joinPoint.proceed();
            String outputString = mapper.writeValueAsString(output);

            if (outputString.length() <= OUTPUT_STRING_SIZE_LIMIT) {
                requestLog.set(OUTPUT, mapper.valueToTree(output));
            } else {
                requestLog.put(OUTPUT, "Output Removed for better readabilty");
            }
            return output;
        } catch (Throwable e) {
            requestLog.put(EXCEPTION, e.getMessage());
            throw e;
        } finally {
            String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestLog);
            if (logOnInfoMode) {
                log.info(BRACES, jsonOutput);
            } else {
                log.debug(BRACES, jsonOutput);
            }
        }
    }

    private ObjectNode processInput(ProceedingJoinPoint joinPoint, String serviceName) {
        ObjectNode logObject = mapper.createObjectNode();
        logObject.put(SERVICE_NAME, serviceName);
        logObject.put(METHOD_NAME,
                ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
        logObject.put(CLASS_NAME, joinPoint.getSignature().getDeclaringTypeName());

        if (joinPoint.getArgs() == null || joinPoint.getArgs().length == 0) {
            return logObject;
        }

        logObject.set(INPUT, mapper.valueToTree(joinPoint.getArgs()));
        return logObject;
    }

}
