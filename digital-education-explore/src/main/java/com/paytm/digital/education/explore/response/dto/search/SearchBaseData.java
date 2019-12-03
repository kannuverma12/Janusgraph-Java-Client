package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchBaseData implements Serializable {

    @JsonProperty("shortlisted")
    private Boolean shortlisted;

    @JsonProperty("interested")
    private Boolean interested;

    @JsonProperty("is_client")
    private Boolean client;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("cta_list")
    private List<CTA> ctaList;

}
