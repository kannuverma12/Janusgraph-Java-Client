package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseSearch {

    @JsonProperty("course_id")
    private long courseId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("level")
    private String level;

    @JsonProperty("study_mode")
    private String studyMode;

    @JsonProperty("duration_in_months")
    private int durationInMonths;

    @JsonProperty("domain_name")
    private String domainName;

    @JsonProperty("exams")
    private List<String> exams;

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("mode")
    private String mode;

    @JsonProperty("fees")
    private long fees;
}
