package com.paytm.digital.education.metrics.aspects;

import com.paytm.digital.education.metrics.DataDogClient;
import com.paytm.digital.education.metrics.MetricConstant;
import com.paytm.digital.education.metrics.annotations.NullValueAlert;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@AllArgsConstructor
public class NullValueAlertAspect {

    private static final Logger log = LoggerFactory.getLogger(NullValueAlertAspect.class);

    private DataDogClient metricClient;

    @Around("@annotation(com.paytm.digital.education.metrics.annotations.NullValueAlert)")
    public Object incrementCount(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        NullValueAlert annotation = method.getAnnotation(NullValueAlert.class);

        String metricName;
        if (annotation.name().isEmpty()) {
            metricName = className + MetricConstant.SEPARATOR + methodName;
        } else {
            metricName = annotation.name();
        }
        List<String> mandatoryParams = null;
        try {
            mandatoryParams =
                    (List<String>) getDynamicValue(signature.getParameterNames(),
                            joinPoint.getArgs(), annotation.mandatoryFields());
        } catch (Exception ex) {
            log.error("NullValueAlertAspect ", ex);
        }
        Object object = null;
        try {
            object = joinPoint.proceed();
        } finally {
            if (Objects.nonNull(object)) {
                if (object instanceof java.util.List) {
                    List<Object> objectList = (List<Object>) object;
                    for (Object objectInstance : objectList) {
                        generateAlert(metricName, mandatoryParams, objectInstance);
                    }
                } else {
                    generateAlert(metricName, mandatoryParams, object);
                }
            }
        }
        return object;
    }

    private void generateAlert(String metricName, List<String> mandatoryParams, Object object) {
        Class targetClass = object.getClass();
        Map<String, Field> fieldMap = getFieldMap(targetClass);
        if (mandatoryParams != null) {
            for (String param : mandatoryParams) {
                try {
                    Field field = fieldMap.get(param);
                    if (Objects.nonNull(field)) {
                        field.setAccessible(true);
                        if (Objects.isNull(field.get(object))) {
                            log.error(
                                    "NullValueAlertAspect , got null value for field '{}' in class"
                                            + " {} for Object {} ",
                                    param, targetClass, object);
                            metricClient
                                    .increment(metricName, "NULL" + targetClass + param);
                        }
                    }
                } catch (Exception ex) {
                    log.error(
                            "NullValueAlertAspect , got null value for field '{}' in class {} "
                                    + "for Object {} ",
                            param, targetClass, object);
                    metricClient.increment(metricName, "NULL" + targetClass + param);
                }
            }
        }
    }

    private Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int index = 0; index < parameterNames.length; index++) {
            context.setVariable(parameterNames[index], args[index]);
        }
        return (Object) parser.parseExpression(key).getValue(context, Object.class);
    }

    private String getFieldValueFromAnnotation(Field field) {
        if (Objects.nonNull(
                field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class))) {
            return field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class)
                    .value();
        }
        return null;
    }

    private Map<String, Field> getFieldMap(Class targetClass) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : targetClass.getDeclaredFields()) {
            String fieldAnnotationValue = getFieldValueFromAnnotation(field);
            if (StringUtils.isNotEmpty(fieldAnnotationValue)) {
                fieldMap.put(fieldAnnotationValue, field);
            } else {
                fieldMap.put(field.getName(), field);
            }
        }
        return fieldMap;
    }

}
