package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ExamType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel
public class CoachingExamCreateRequest {

    @NotNull
    @JsonProperty("institute_id")
    private Long instituteId;

    @NotNull
    @JsonProperty("exam_type")
    private ExamType examType;

    @NotNull
    @JsonProperty("exam_name")
    private String examName;

    @JsonProperty("exam_description")
    private String examDescription;

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("stream_id")
    private Long streamId;

    @JsonProperty("exam_duration")
    private String examDuration;

    @JsonProperty("maximum_marks")
    private Double maximumMarks;

    @NotEmpty
    @JsonProperty("exam_date")
    private List<LocalDateTime> examDate;

    @JsonProperty("active")
    private Boolean active = Boolean.TRUE;

    @JsonProperty("eligibility")
    private String eligibility;

    @JsonProperty("priority")
    private Integer priority;
}
