package com.paytm.digital.education.explore.database.entity;

import com.paytm.digital.education.explore.enums.LeadPartner;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Career360LeadResponse extends BaseLeadResponse {

    @Field("error_code")
    private Integer errorCode;

    @Field("message")
    private String message;

    @Field("cta_status")
    private Integer ctaStatus;

    @Field("cta_message")
    private String ctaMessage;

    @Field("lead_partner")
    private LeadPartner leadPartner;
}
