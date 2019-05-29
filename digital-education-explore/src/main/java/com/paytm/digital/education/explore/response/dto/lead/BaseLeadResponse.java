package com.paytm.digital.education.explore.response.dto.lead;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseLeadResponse {

    @JsonProperty("user_id")
    @Field(Constants.USER_ID)
    private Long userId;

    @JsonProperty("entity_id")
    @Field(Constants.ENTITY_ID)
    private Long entityId;

    @JsonProperty("request_type")
    @Field(Constants.REQUEST_TYPE)
    private Integer requestType;

    @JsonProperty("entity_type")
    @Field(Constants.ENTITY_TYPE)
    private EducationEntity entityType;

    @JsonProperty("error_code")
    @Field(Constants.ERROR_CODE)
    private Integer errorCode;

    @JsonProperty("message")
    @Field(Constants.MESSAGE)
    private String message;

    @JsonProperty("cta_status")
    @Field(Constants.CTA_STATUS)
    private Integer ctaStatus;

    @JsonProperty("cta_message")
    @Field(Constants.CTA_MESSAGE)
    private String ctaMessage;

    @JsonIgnore
    @Field(Constants.CREATED_AT)
    private Date createdAt;

    @JsonIgnore
    @Field(Constants.UPDATED_AT)
    private Date updatedAt;


    public static class Constants {
        static final String CTA_MESSAGE  = "cta_message";
        static final String CTA_STATUS   = "cta_status";
        static final String MESSAGE      = "message";
        static final String ERROR_CODE   = "error_code";
        static final String ENTITY_TYPE  = "entity_type";
        static final String ENTITY_ID    = "entity_id";
        static final String REQUEST_TYPE = "request_type";
        static final String CREATED_AT   = "created_at";
        static final String UPDATED_AT   = "updated_at";
        static final String USER_ID      = "user_id";
    }

}
