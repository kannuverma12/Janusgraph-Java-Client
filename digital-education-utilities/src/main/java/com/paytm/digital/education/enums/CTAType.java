package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public enum CTAType {

    @JsonProperty("shortlist")
    SHORTLIST,

    @JsonProperty("lead")
    LEAD,

    @JsonProperty("fee")
    FEE,

    @JsonProperty("predictor")
    PREDICTOR,

    @JsonProperty("brochure")
    BROCHURE,

    @JsonProperty("share")
    SHARE,

    @JsonProperty("compare")
    COMPARE,

    @JsonProperty("forms")
    FORMS;


}
