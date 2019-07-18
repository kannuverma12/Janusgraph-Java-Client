package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FilterData {

    @JsonProperty("name")
    private Object name;

    @JsonProperty("display_name")
    private String displayName;
    
}
