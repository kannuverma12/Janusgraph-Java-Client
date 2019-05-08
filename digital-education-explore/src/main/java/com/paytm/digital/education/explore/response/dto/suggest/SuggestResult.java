package com.paytm.digital.education.explore.response.dto.suggest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuggestResult {

    @JsonProperty("entity_id")
    private long entityId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    public SuggestResult(long entityId, String officialName) {
        this.entityId = entityId;
        this.officialName = officialName;
    }

}
