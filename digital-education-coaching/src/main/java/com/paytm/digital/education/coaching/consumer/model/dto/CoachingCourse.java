package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourse {

    private Long       courseId;
    private String     courseName;
    private String     description;
    private String     targetExam;
    private String     eligibility;
    private Integer    duration;
    private BigDecimal price;
    private String     imageUrl;
    private String     courseLevel;
    private Integer    priority;
    private String     courseType;
}
