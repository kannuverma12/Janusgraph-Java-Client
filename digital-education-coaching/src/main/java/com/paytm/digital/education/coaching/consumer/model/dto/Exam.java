package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Exam {

  private long id;
  private String name;
  private String image;
  private String admissionInto;
  private String conductedBy;
  private int priority;
}
