package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.dto.OfficialAddress;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseInstituteDetail {

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("is_client")
    private Boolean isClient;

    @JsonProperty("interested")
    private Boolean interested;
}
