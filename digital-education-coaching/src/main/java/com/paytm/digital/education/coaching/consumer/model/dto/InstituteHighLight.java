package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstituteHighLight {

    private String key;
    private String value;
    private String logo;
}