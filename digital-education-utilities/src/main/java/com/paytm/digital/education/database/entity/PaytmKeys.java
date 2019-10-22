package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaytmKeys {

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
