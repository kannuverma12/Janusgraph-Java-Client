package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchBaseData {

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @JsonProperty("interested")
    private boolean interested;

    @JsonProperty("is_client")
    private boolean client;

    @JsonProperty("logo_url")
    private String logoUrl;

}
