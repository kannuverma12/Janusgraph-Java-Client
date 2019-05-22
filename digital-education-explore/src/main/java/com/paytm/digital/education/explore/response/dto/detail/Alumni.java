package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alumni {

    @JsonProperty("alumni_name")
    private String alumniName;

    @JsonProperty("current_designation")
    private String currentDesignation;

    @JsonProperty("alumni_photo")
    private String alumniPhoto;
}
