package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BannerData implements Serializable {

    private static final long serialVersionUID = 1598448888598271179L;

    @JsonProperty("url")
    private String url;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("rurl")
    private String rurl;

}
