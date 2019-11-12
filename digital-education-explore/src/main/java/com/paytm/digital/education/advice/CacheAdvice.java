package com.paytm.digital.education.advice;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.explore.service.RedisOrchestrator;
import com.paytm.digital.education.explore.service.impl.MethodEnclosedInProceedingJoinPoint;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.springframework.data.util.Pair.toMap;
import static org.springframework.data.util.StreamUtils.zip;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheAdvice {

    private static final Logger log = LoggerFactory.getLogger(CacheAdvice.class);
    private static final PropertyUtilsBean PROPERTY_UTILS_BEAN = new PropertyUtilsBean();
    private static final String FAILED_TO_ACCESS_FIELD_ERROR = "Failed to access field {} in bean {}";
    private static final String OBJECT_NOT_KEYABLE_ERROR = "Object {} does not have any way to convert to key";
    private static final String KEY_DELIMITER = ".";
    private static final String CACHE_NAME_DELIMITER = "##";

    private final RedisOrchestrator redisOrchestrator;

    @Around("@annotation(com.paytm.digital.education.annotation.EduCache)")
    public Object interceptCachedMethodCalls(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        Map<String, Object> params = zip(
                stream(parameterNames), stream(args), Pair::of).collect(toMap());
        Method method = signature.getMethod();
        EduCache eduCacheAnnotation = method.getAnnotation(EduCache.class);
        String[] keys = eduCacheAnnotation.keys();
        String cacheName = eduCacheAnnotation.cache();
        Object[] valuesProvidingKeys = keys.length == 0 ? args : extractValuesFromParams(params, keys);
        String cacheKey = cacheName + CACHE_NAME_DELIMITER
                + stream(valuesProvidingKeys).map(CacheAdvice::fetchKey).collect(joining(KEY_DELIMITER));
        return redisOrchestrator.get(cacheKey, new MethodEnclosedInProceedingJoinPoint(pjp));
    }

    private static String fetchKey(Object o) {
        if (o instanceof Number || o instanceof CharSequence || o instanceof Class) {
            return o.toString();
        } else if (o instanceof CacheKeyable) {
            CacheKeyable cacheKeyable = (CacheKeyable) o;
            return join(KEY_DELIMITER, cacheKeyable.cacheKeys());
        } else if (o instanceof Collection) {
            Collection c = (Collection) o;
            Stream<String> keys = c.stream().map(CacheAdvice::fetchKey);
            return keys.collect(joining(KEY_DELIMITER));
        } else {
            log.error(OBJECT_NOT_KEYABLE_ERROR, o);
            throw new RuntimeException(OBJECT_NOT_KEYABLE_ERROR);
        }
    }

    private Object[] extractValuesFromParams(Map<String, Object> params, String[] keys) {
        Object[] values = new Object[keys.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                values[i] = PROPERTY_UTILS_BEAN.getProperty(params, keys[i]);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(FAILED_TO_ACCESS_FIELD_ERROR, e, keys[i], params);
                throw new RuntimeException(FAILED_TO_ACCESS_FIELD_ERROR);
            }
        }
        return values;
    }
}
