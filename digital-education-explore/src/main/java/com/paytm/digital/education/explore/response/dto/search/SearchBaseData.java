package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchBaseData {

    @JsonProperty("shortlisted")
    private Boolean shortlisted;

    @JsonProperty("interested")
    private Boolean interested;

    @JsonProperty("is_client")
    private Boolean client;

    @JsonProperty("logo_url")
    private String logoUrl;

}
