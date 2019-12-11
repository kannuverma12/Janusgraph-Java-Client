package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaytmKeys implements Serializable {

    private static final long serialVersionUID = 373337147728599200L;

    public static class Constants {

        public static final String PAYTM_KEYS = "paytm_keys";

    }

    @Field("paytm_rank")
    @JsonProperty("paytm_rank")
    private Long paytmRank;

    @Field("paytm_partner_rank")
    @JsonProperty("paytm_partner_rank")
    private Long paytmPartnerRank;

    @Field("paytm_description")
    @JsonProperty("paytm_description")
    private String paytmDescription;

    @Field("paytm_tag")
    @JsonProperty("paytm_tag")
    private String paytmTag;

}
