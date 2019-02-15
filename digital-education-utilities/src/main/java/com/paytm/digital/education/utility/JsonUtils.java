package com.paytm.digital.education.utility;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (Exception ex) {
            log.error("Exception caught in parsing json. ", ex);
        }
        return StringUtils.EMPTY;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonParseException jpe) {
            log.error("Json Parsing Error. Malformed Json Content: [ " + json + " ]", jpe);
        } catch (JsonMappingException jme) {
            log.error("Json Mapping Error while converting Json string: [ " + json
                    + " ] to object type: " + clazz, jme);
        } catch (IOException ioe) {
            log.error("I/O Error while converting Json string: [ " + json + " ] to object type: "
                    + clazz, ioe);
        } catch (Exception ex) {
            log.error("Error caught while converting Json string: [ " + json + " ] to object type: "
                    + clazz, ex);
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonParseException jpe) {
            log.error("Json Parsing Error. Malformed Json Content: [ " + json + " ]", jpe);
        } catch (JsonMappingException jme) {
            log.error("Json Mapping Error while converting Json string: [ " + json
                    + " ] to object type: " + typeReference.getType(), jme);
        } catch (IOException ioe) {
            log.error("I/O Error while converting Json string: [ " + json + " ] to object type: "
                    + typeReference.getType(), ioe);
        } catch (Exception ex) {
            log.error("Error caught while converting Json string: [ " + json + " ] to object type: "
                    + typeReference.getType(), ex);
        }
        return null;
    }
}
