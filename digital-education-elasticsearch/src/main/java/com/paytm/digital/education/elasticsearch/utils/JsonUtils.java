package com.paytm.digital.education.elasticsearch.utils;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T convertValue(Map<String, Object> sourceMap, Class<T> clazz) {
        return objectMapper.convertValue(sourceMap, clazz);
    }

}

