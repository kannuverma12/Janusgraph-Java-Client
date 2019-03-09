package com.paytm.digital.education.explore.request.dto.search;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_OFFSET;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_REQUEST_MAX_LIMIT;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.sro.request.FieldsAndFieldGroupRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest extends FieldsAndFieldGroupRequest {

    @JsonProperty("term")
    private String term;

    @JsonProperty("filter")
    private Map<String, List<Object>> filter;

    @JsonProperty("entity")
    private EducationEntity entity;

    @JsonProperty("offset")
    @Min(0)
    @NotNull
    private Integer offset = DEFAULT_OFFSET;

    @JsonProperty("limit")
    @Min(0)
    @Max(SEARCH_REQUEST_MAX_LIMIT)
    @NotNull
    private Integer limit = DEFAULT_SIZE;

    @JsonProperty("fetch_filter")
    private boolean fetchFilter = true;
   
}
