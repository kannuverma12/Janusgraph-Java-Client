package com.paytm.digital.education.explore.es.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseSearch {

    @JsonProperty("institute_id")
    private long instituteId;

    @JsonProperty("parent_institute_id")
    private long parentInstituteId;

    @JsonProperty("institute_official_name")
    private String instituteName;

    @JsonProperty("course_id")
    private long courseId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("seats")
    private Integer seats;

    @JsonProperty("degree")
    private List<String> degree;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("level")
    private String level;

    @JsonProperty("study_mode")
    private String studyMode;

    @JsonProperty("duration_in_months")
    private Integer durationInMonths;

    @JsonProperty("domain_name")
    private List<String> domainName;

    @JsonProperty("exams")
    private List<String> exams;

    @JsonProperty("fees")
    private Long fees;

}
