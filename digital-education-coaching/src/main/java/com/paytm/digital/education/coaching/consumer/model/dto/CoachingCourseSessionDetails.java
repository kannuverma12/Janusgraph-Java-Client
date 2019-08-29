package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoachingCourseSessionDetails {

    private String key;
    private String value;
    private int    priority;
}
