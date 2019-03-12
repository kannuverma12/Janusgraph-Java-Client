package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class Properties {

    @Field("key")
    private String key;

    @Field("component")
    private String component;

    @Field("namespace")
    private String namespace;

    @Field("attributes")
    private Map<String, Object> attributes;

}
