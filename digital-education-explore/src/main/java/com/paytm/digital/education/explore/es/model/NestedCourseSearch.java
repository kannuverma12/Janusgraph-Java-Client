package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedCourseSearch {

    @JsonProperty("course_id")
    private long courseId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("degree")
    private List<String> degree;

    @JsonProperty("level")
    private String level;

    @JsonProperty("study_mode")
    private String studyMode;

    @JsonProperty("duration_in_months")
    private int durationInMonths;

    @JsonProperty("domain_name")
    private List<String> domainName;

    @JsonProperty("exams")
    private List<String> exams;

    @JsonProperty("fees")
    private long fees;
}
