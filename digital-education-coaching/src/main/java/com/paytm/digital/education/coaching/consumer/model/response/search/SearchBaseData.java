package com.paytm.digital.education.coaching.consumer.model.response.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
