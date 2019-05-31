package com.paytm.digital.education.explore.thirdparty.lead;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Career360EntityType;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

@Data
public class Career360LeadRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("city_id")
    private Long cityId;

    @JsonProperty("state_id")
    private Long stateId;

    @JsonProperty("request_type")
    private Integer requestType;

    @JsonProperty("paytm_customer_id")
    private Long paytmCustomerId;

    @JsonProperty("entity_type")
    private Career360EntityType entityType;

    @JsonProperty("entity_id")
    private Long entityId;

}
