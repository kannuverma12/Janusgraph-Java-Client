package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.ExamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingExamDTO {

    private Long coachingExamId;

    private Long instituteId;

    private ExamType examType;

    private String examName;

    private String examDescription;

    private Long programId;

    private Long streamId;

    private String examDuration;

    private Double maximumMarks;

    private List<LocalDateTime> examDate;

    private String eligibility;

    private Integer priority;

    private Boolean isEnabled;

}
