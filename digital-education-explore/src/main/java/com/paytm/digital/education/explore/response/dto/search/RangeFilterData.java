package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeFilterData extends FilterData {

    @JsonProperty("min")
    private double minValue;

    @JsonProperty("max")
    private double maxValue;

    @JsonProperty("selected_values")
    private List<List<Integer>> selectedValues;

    //    @JsonProperty("min_selected")
    //    private Double minSelected;
    //
    //    @JsonProperty("max_selected")
    //    private Double maxSelected;

}
