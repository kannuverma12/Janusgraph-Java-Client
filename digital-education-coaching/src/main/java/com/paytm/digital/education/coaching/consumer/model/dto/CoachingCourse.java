package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CoachingCourse {

    private long       courseId;
    private String     courseName;
    private String     description;
    private String     targetExam;
    private String     eligibility;
    private int        duration;
    private BigDecimal price;
    private String     imageUrl;
    private String     courseLevel;
    private int        priority;
}
