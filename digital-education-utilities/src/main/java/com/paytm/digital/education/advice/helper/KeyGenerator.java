package com.paytm.digital.education.advice.helper;

import com.paytm.digital.education.advice.CacheKeyable;
import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.exception.UnableToAccessBeanPropertyException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.paytm.digital.education.utility.CommonUtils.sortMapByKeys;
import static com.paytm.digital.education.utility.JsonUtils.toJson;
import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.springframework.data.util.StreamUtils.zip;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@Service
public class KeyGenerator {

    private static final Logger log = LoggerFactory.getLogger(KeyGenerator.class);
    private static final PropertyUtilsBean PROPERTY_UTILS_BEAN = new PropertyUtilsBean();
    private static final String FAILED_TO_ACCESS_FIELD_ERROR = "Failed to access field {} in bean {}";
    private static final String OBJECT_NOT_KEYABLE_ERROR = "Object {} does not have any way to convert to key";
    private static final String KEY_DELIMITER = ".";
    private static final String CACHE_NAME_DELIMITER = "##";
    private static final String KEY_LENGTH_EXCEEDED_WARNING_TEMPLATE =
            "Generated key length is greater than maximum allowed length of - {}. Key {} will be abbreviated to {}.";

    private final int maxKeyLength;

    public KeyGenerator(@Value("${twemproxy.max.key.length}") int maxKeyLength) {
        this.maxKeyLength = maxKeyLength;
    }

    public String generateKey(
            EduCache eduCacheAnnotation, Class declaringClass, String methodName, String[] params, Object[] values) {
        String[] keys = eduCacheAnnotation.keys();
        Object[] valuesProvidingKeys = keys.length == 0 ? values : extractValuesFromParams(keys, params, values);
        String fullKey = eduCacheAnnotation.cache() + CACHE_NAME_DELIMITER
                + concat(
                        of(declaringClass.getCanonicalName(), methodName),
                        stream(valuesProvidingKeys).map(KeyGenerator::fetchKey)
                ).collect(joining(KEY_DELIMITER));
        String abbreviatedKey = abbreviate(fullKey, maxKeyLength - 1);
        if (fullKey.length() >= maxKeyLength) {
            log.warn(KEY_LENGTH_EXCEEDED_WARNING_TEMPLATE, maxKeyLength, fullKey, abbreviatedKey);
        }
        return abbreviatedKey;
    }

    private Object[] extractValuesFromParams(String[] keys, String[] params, Object[] values) {
        Map<String, Object> paramsMap = zip(stream(params), stream(values), Pair::of)
                .collect(HashMap::new, (m, v) -> m.put(v.getLeft(), v.getRight()), HashMap::putAll);
        Object[] keyValues = new Object[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            try {
                keyValues[i] = PROPERTY_UTILS_BEAN.getProperty(paramsMap, keys[i]);
            } catch (IllegalArgumentException e) {
                keyValues[i] = null;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(FAILED_TO_ACCESS_FIELD_ERROR, e, keys[i], paramsMap);
                throw new UnableToAccessBeanPropertyException(paramsMap, keys[i]);
            }
        }
        return keyValues;
    }

    private static String fetchKey(Object o) {
        if (Objects.isNull(o)) {
            return EMPTY_STRING;
        } else if (o instanceof Number || o instanceof Boolean || o instanceof CharSequence) {
            return o.toString();
        } else if (o instanceof Enum) {
            return ((Enum) o).name();
        } else if (o instanceof Map) {
            return toJson(sortMapByKeys((Map) o));
        } else if (o instanceof Class) {
            return ((Class) o).getCanonicalName();
        } else if (o instanceof CacheKeyable) {
            CacheKeyable cacheKeyable = (CacheKeyable) o;
            return join(KEY_DELIMITER, cacheKeyable.cacheKeys());
        } else if (o instanceof Collection) {
            Collection c = (Collection) o;
            Stream<String> keys = c.stream().map(KeyGenerator::fetchKey);
            return keys.collect(joining(KEY_DELIMITER));
        } else {
            log.warn(OBJECT_NOT_KEYABLE_ERROR, o);
            return o.toString();
        }
    }
}
