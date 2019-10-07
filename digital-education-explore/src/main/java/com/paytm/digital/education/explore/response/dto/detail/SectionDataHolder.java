package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.ExamSectionType;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionDataHolder {

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("snippet_text")
    private String snippetText;

    @JsonProperty("key")
    private String key;

}
