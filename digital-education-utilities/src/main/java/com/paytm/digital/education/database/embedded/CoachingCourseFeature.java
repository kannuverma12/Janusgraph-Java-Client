package com.paytm.digital.education.database.embedded;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoachingCourseFeature {

    private long   featureId;
    private String featureName;
    private String featureLogo;
    private String featureDescription;
    private int    priority;
}
