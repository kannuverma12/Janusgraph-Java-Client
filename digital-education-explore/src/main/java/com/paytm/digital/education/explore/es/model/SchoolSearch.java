package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolSearch {

    @JsonProperty("school_id")
    private long schoolId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("names")
    private List<String> names;

    @JsonProperty("area_name")
    private String areaName;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("year_of_estd")
    private int establishmentYear;

    @JsonProperty("campus_name")
    private String campusName;

    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("image_link")
    private String imageLink;

    @JsonProperty("lang_medium")
    private List<String> languagesMedium;

    @JsonProperty("boards")
    private List<SchoolBoard> boards;

}
