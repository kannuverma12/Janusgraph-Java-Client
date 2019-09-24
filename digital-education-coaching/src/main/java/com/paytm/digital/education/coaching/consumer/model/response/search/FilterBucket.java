package com.paytm.digital.education.coaching.consumer.model.response.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterBucket {

    @JsonProperty("key")
    private String value;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("count")
    private long docCount;

    @JsonProperty("selected")
    private boolean isSelected;

}
