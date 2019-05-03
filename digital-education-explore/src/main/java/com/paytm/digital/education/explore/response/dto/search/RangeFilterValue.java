package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RangeFilterValue {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("key")
    private List<Double> values;

    @JsonProperty("selected")
    private boolean selected;
}
