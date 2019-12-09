package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Placement implements Serializable {

    private static final long serialVersionUID = 4804286047261626936L;

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("salary")
    private Integer salary;

    @JsonProperty("label")
    private String label;
}
