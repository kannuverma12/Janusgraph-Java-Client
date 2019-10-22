package com.paytm.digital.education.elasticsearch.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class JsonUtils {

    private static Logger log  = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T convertValue(Map<String, Object> sourceMap, Class<T> clazz) {
        return objectMapper.convertValue(sourceMap, clazz);
    }

    public static String toJson(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (Exception ex) {
            log.error("Exception caught in parsing json. ", ex);
        }
        return StringUtils.EMPTY;
    }

}

