package com.paytm.digital.education.explore.response.dto.suggest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.dto.OfficialAddress;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestResult {

    @JsonProperty("entity_id")
    private long entityId;

    @JsonProperty("state_id")
    private Long stateId;

    @JsonProperty("city_id")
    private Long cityId;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    public SuggestResult(long entityId, String officialName) {
        this.entityId = entityId;
        this.officialName = officialName;
    }

}
