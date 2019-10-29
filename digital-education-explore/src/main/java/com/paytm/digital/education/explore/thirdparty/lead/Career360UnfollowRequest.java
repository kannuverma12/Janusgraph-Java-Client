package com.paytm.digital.education.explore.thirdparty.lead;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.Career360EntityType;
import lombok.Data;

@Data
public class Career360UnfollowRequest {

    @JsonProperty("paytm_customer_id")
    private Long paytmCustomerId;

    @JsonProperty("entity_type")
    private Career360EntityType entityType;

    @JsonProperty("entity_id")
    private Long entityId;

}
