package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Placement {

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("salary")
    private Integer salary;

    @JsonProperty("label")
    private String label;
}
