package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    @JsonProperty("results")
    private SearchResult              results;

    @JsonProperty("search_term")
    private String                    searchTerm;

    @JsonProperty("filters")
    private List<FilterData>          filters;

    @JsonProperty("total")
    private long                      total;

    @JsonIgnore
    private Map<Long, SearchBaseData> entityDataMap;

    public SearchResponse(String term) {
        this.searchTerm = term;
    }

    @JsonIgnore
    public boolean isSearchResponse() {
        if (this.results != null && !CollectionUtils.isEmpty(this.results.getValues())) {
            return true;
        }
        return false;
    }

}
