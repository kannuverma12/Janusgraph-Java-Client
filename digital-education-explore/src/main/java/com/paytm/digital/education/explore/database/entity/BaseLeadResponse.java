package com.paytm.digital.education.explore.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class BaseLeadResponse {

    @Field("interested")
    private boolean interested;

    @Field("error_code")
    private Integer errorCode;

    @Field("message")
    private String message;

    @Field("cta_message")
    private String ctaMessage;

    @Field("cta_code")
    private Integer ctaCode;

}
