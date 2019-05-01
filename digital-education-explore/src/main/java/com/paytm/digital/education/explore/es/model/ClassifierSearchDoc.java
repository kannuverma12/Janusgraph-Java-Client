package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.ClassifierDocType;
import com.paytm.digital.education.explore.enums.ClassifierEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassifierSearchDoc {

    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("filter")
    private Map<String, List<String>> filters;

    @JsonProperty("curated_keywords")
    private List<String> genericKeywords;

    @JsonProperty("sort_order")
    private List<ClassifierSortField> sortOrder;

    @JsonProperty("type")
    private ClassifierDocType type;

    @JsonProperty("entity")
    private ClassifierEntity entity;

    @JsonProperty("mapped_keyword")
    private String mappedKeyword;
}
