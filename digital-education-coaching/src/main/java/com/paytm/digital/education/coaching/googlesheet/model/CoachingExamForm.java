package com.paytm.digital.education.coaching.googlesheet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingExamForm {
    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("exam_name")
    private String examName;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("exam_description")
    private String examDescription;

    @JsonProperty("exam_date")
    private String examDate;

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("exam_type")
    private String examType;

    @JsonProperty("exam_duration")
    private Integer examDuration;

    @JsonProperty("marks")
    private Double marks;

    @JsonProperty("status")
    private String status;
}
