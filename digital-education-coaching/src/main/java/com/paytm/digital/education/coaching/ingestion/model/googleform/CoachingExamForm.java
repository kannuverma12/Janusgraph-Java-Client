package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingExamForm {

    private Long   coachingExamId;
    private Long   instituteId;
    private String examType;
    private String examName;
    private String examDescription;
    private String streamIds;
    private String courseIds;
    private String examDuration;
    private Double maximumMarks;
    private String examDates;
    private String eligibility;
    private String numberOfQuestions;

    private Integer globalPriority;
    private String  statusActive;
}
