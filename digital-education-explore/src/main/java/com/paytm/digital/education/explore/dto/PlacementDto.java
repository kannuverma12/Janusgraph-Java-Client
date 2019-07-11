package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacementDto {
    @JsonProperty("year")
    private Integer year;

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("median")
    private Integer median;

    @JsonProperty("average")
    public Integer average;

    @JsonProperty("maximum")
    public Integer maximum;

    @JsonProperty("minimum")
    public Integer minimum;
}
