package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FilterBucket {

    @JsonProperty("key")
    private String value;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("count")
    private long   docCount;

    @JsonProperty("selected")
    private boolean isSelected;
}
