package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolSearchData extends SearchBaseData {

    @JsonProperty("school_id")
    private long schoolId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

}
