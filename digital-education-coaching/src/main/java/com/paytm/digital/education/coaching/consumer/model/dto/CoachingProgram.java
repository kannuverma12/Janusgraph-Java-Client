package com.paytm.digital.education.coaching.consumer.model.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class CoachingProgram {

  private long programId;
  private String programName;
  private String description;
  private String targetExam;
  private String eligibility;
  private int duration;
  private BigDecimal price;
  private String imageUrl;
  private String programLevel;
  private int priority;
}
