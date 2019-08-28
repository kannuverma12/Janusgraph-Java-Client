package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult {

    @JsonProperty("entity")
    private EducationEntity entity;

    @JsonProperty("values")
    private List<SearchBaseData> values;
}
