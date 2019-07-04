package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleResource {

    @Field("type")
    @JsonProperty("type")
    private String type;

    @Field("url")
    @JsonProperty("url")
    private String url;

    @Field("label")
    @JsonProperty("label")
    private String label;

    @Field("description")
    @JsonProperty("description")
    private String description;
}
