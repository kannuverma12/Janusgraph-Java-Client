package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class FacilityResponse implements Serializable {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("logo_url")
    private String logoUrl = "/placeholder.svg?v=2";

    public FacilityResponse(String name) {
        this.name = name;
        this.displayName = name;
    }
}
