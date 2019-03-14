package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchBaseData {

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @JsonProperty("get_in_touch")
    private boolean getInTouch;

    @JsonProperty("logo_url")
    private String logoUrl;

}
