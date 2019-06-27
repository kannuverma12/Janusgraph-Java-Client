package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Media {

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("year")
    @JsonProperty("year")
    private Integer year;

    @Field("description")
    @JsonProperty("description")
    private String description;

    @Field("images")
    @JsonProperty("images")
    private List<String> images;

    @Field("videos")
    @JsonProperty("videos")
    private List<String> videos;

    @Field("failed_media")
    @JsonProperty("failed_media")
    private List<String> failedMedia;
}
