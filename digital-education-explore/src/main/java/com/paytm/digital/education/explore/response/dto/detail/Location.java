package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location implements Serializable {

    private static final long serialVersionUID = 104246742673329178L;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

}
