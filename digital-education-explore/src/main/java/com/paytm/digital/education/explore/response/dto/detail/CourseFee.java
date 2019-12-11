package com.paytm.digital.education.explore.response.dto.detail;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseFee implements Serializable {
    private static final long serialVersionUID = 1559795930100121669L;
    @JsonProperty("fee")
    private Integer fee;

    @JsonProperty("caste_group")
    private String casteGroup;

    @JsonProperty("caste_group_display_name")
    private String casteGroupDisplay;
}

