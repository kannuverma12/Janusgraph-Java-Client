package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Stream {

    private long   id;
    private String name;
    private String logo;
    private String specialText;
}
