package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoachingProgramFeature {

  private long featureId;
  private String featureName;
  private String featureLogo;
  private String featureDescription;
  private int priority;
}
