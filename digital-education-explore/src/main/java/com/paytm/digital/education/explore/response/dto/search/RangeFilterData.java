package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeFilterData extends FilterData {

    @JsonProperty("min")
    private double minValue;

    @JsonProperty("max")
    private double maxValue;

    @JsonProperty("min_selected")
    private double minSelected;

    @JsonProperty("max_selected")
    private double maxSelected;

}
