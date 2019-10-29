package com.paytm.digital.education.coaching.consumer.model.response.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeFilterValue extends FilterData {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("key")
    private List<Double> values;

    @JsonProperty("selected")
    private boolean selected;
}
