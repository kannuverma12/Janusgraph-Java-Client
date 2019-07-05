package com.paytm.digital.education.coaching.googlesheet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopRankedAchievedForm {
    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("exam_year")
    private Integer examYear;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("student_photo")
    private String studentPhoto;

    @JsonProperty("course_studied")
    private String courseStudied;

    @JsonProperty("batch")
    private String batch;

    @JsonProperty("rank_obtained")
    private Integer rankObtained;

    @JsonProperty("college_admitted")
    private String collegeAdmitted;
}
