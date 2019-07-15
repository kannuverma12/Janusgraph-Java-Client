package com.paytm.digital.education.elasticsearch.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public class JsonUtils {

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

