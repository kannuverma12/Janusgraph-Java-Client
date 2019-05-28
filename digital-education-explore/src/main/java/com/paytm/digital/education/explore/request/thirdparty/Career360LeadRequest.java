package com.paytm.digital.education.explore.request.thirdparty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Career360EntityType;
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
    private Long requestType;

    @JsonProperty("paytm_customer_id")
    private Long paytmCustomerId;

    @JsonProperty("entity_type")
    private Career360EntityType entityType;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("api_key")
    private String apiKey = "Pekfrtyuyuyerwdghjhff#54555hhfghfghfh";

}
