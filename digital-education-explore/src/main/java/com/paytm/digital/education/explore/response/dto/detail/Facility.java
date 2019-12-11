package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Facility implements Serializable {

    private static final long serialVersionUID = -4216795721386701120L;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("name")
    private String name;

}
