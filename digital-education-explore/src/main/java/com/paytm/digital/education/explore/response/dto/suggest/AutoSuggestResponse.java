package com.paytm.digital.education.explore.response.dto.suggest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoSuggestResponse {

    @JsonProperty("data")
    private List<AutoSuggestData> data;

    @JsonIgnore
    private Map<String, Map<Long, SuggestResult>> perEntitySuggestMap;

}
