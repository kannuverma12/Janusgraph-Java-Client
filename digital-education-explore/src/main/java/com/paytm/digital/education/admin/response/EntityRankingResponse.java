package com.paytm.digital.education.admin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityRankingResponse {

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("paytm_rank")
    private Long paytmRank;

    @JsonProperty("paytm_partner_rank")
    private Long paytmPartnerRank;

    @JsonProperty("description")
    private String description;

}
