package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gallery {

    @Field("logo")
    @JsonProperty("logo")
    private String                    logo;

    @Field("images")
    @JsonProperty("images")
    private Map<String, List<String>> images;

    @Field("videos")
    @JsonProperty("videos")
    private Map<String, List<String>> videos;
}
