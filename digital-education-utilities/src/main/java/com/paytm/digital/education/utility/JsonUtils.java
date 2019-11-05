package com.paytm.digital.education.utility;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper objectMapper =
            new ObjectMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    public static String toJson(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (Exception ex) {
            log.error("Exception caught in parsing json, input: {}, exception: ",ex,  input);
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

    public static <T> T convertValue(Object object, TypeReference typeReference) {
        try {
            return objectMapper.convertValue(object, typeReference);
        } catch (IllegalArgumentException ex) {
            log.error("Illegal argument : [ " + object.toString() + " ]", ex);
        } catch (Exception ex) {
            log.error("Error caught while converting object : [ " + object.toString()
                    + " ] to object type: " + typeReference.getType(), ex);
        }
        return null;
    }

    public static <T> T convertValue(Object object, Class<T> type) {
        try {
            return objectMapper.convertValue(object, type);
        } catch (IllegalArgumentException ex) {
            log.error("Illegal argument : [ " + object.toString() + " ]", ex);
        } catch (Exception ex) {
            log.error("Error caught while converting object : [ " + object.toString()
                    + " ] to object type: " + type, ex);
        }
        return null;
    }
}
