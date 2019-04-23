package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MultipleRangeData extends FilterData {

    @JsonProperty("values")
    private List<RangeFilterValue> values;

}
