package com.paytm.digital.education.elasticsearch.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T convertValue(Map<String, Object> sourceMap, Class<T> clazz) {
        return objectMapper.convertValue(sourceMap, clazz);
    }

}

