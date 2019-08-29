package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacilityResponse {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("logo_url")
    private String logoUrl = "placeholder.svg";

    public FacilityResponse(String name) {
        this.name = name;
        this.displayName = name;
    }
}
