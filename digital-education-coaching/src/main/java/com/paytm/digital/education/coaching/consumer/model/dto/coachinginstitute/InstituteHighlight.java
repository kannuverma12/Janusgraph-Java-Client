package com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstituteHighlight {

    private String key;
    private String value;
    private String logo;
}