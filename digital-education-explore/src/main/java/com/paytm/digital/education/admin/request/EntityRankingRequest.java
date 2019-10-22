package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class EntityRankingRequest {

    @NotNull
    @Min(1)
    @JsonProperty("entity_id")
    private Long entityId;

    @NotNull
    @Min(-10)
    @JsonProperty("paytm_rank")
    private Long paytmRank;

    @JsonProperty("paytm_partner_rank")
    private Long paytmPartnerRank;

    @JsonProperty("paytm_description")
    private String paytmDescription;

}
