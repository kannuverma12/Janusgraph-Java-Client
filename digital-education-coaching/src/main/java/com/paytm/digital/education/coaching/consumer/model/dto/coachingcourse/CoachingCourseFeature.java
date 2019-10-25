package com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseFeature {

    private long   featureId;
    private String featureName;
    private String featureLogo;
    private String featureDescription;
    private int    priority;
}
