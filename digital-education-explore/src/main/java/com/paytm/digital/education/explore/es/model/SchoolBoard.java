package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolBoard {

    @JsonProperty("board_name")
    private String boardName;

    @JsonProperty("education_level")
    private String educationLevel;

    @JsonProperty("class_from")
    private String classFrom;

    @JsonProperty("class_to")
    private String classTo;

    @JsonProperty("gender_accepted")
    private String gendersAccepted;

    @JsonProperty("ownership")
    private String ownership;

    @JsonProperty("affiliation_type")
    private String affiliationType;

    @JsonProperty("residential_status")
    private List<String> residentialStatus;

    @JsonProperty("fees")
    private long fees;

}