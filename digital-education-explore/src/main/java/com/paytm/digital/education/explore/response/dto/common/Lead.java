package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Lead {

    @JsonProperty("interested")
    private Boolean interested;

    @JsonProperty("error")
    private boolean error;

}
