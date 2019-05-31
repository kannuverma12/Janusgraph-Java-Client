package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseLeadResponse {

    @Field("lead_id")
    private String leadId;

    @Field("interested")
    private Boolean interested;

    @Field("error_code")
    private Integer errorCode;

    @Field("message")
    private String message;

    @Field("cta_message")
    private String ctaMessage;

    @Field("cta_code")
    private Integer ctaCode;

}
