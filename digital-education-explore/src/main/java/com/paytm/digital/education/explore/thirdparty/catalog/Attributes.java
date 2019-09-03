package com.paytm.digital.education.explore.thirdparty.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attributes {

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("operator")
    private String operator;

    @JsonProperty("operator_label")
    private String operatorLabel;

    @JsonProperty("school")
    private String school;

    @JsonProperty("paytype")
    private String payType;

    @JsonProperty("paytype_label")
    private String payTypeLabel;

}
