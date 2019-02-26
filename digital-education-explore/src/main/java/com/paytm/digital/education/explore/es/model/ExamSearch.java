package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExamSearch {

    @JsonProperty("exam_id")
    private long examId;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category; //Stream

    @JsonProperty("mode")
    private String mode;

    @JsonProperty("level")
    private String level;

    @JsonProperty("exam_date")
    private Date examDate;

    @JsonProperty("application_date")
    private Date applicationDate;

    @JsonProperty("result_date")
    private Date resultDate;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("exam_language")
    private List<String> examLanguage;

    @JsonProperty("type")
    private String type;

}
