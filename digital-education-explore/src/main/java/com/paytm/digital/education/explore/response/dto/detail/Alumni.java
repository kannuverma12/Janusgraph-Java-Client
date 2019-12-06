package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alumni implements Serializable {

    private static final long serialVersionUID = 5484461257927001056L;

    @JsonProperty("alumni_name")
    private String alumniName;

    @JsonProperty("current_designation")
    private String currentDesignation;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("alumni_photo")
    private String alumniPhoto;
}
