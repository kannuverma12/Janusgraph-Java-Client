package com.paytm.digital.education.explore.response.dto.suggest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoSuggestResponse {

    @JsonProperty("data")
    private List<AutoSuggestData> data;

}
