package com.paytm.digital.education.coaching.producer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachingProgramSessionDetails {

    private String key;
    private String value;
    private int    priority;
}
