package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionDataHolder implements Serializable {
    private static final long serialVersionUID = -8706022718032641769L;
    @JsonProperty("icon")
    private String icon;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("snippet_text")
    private String snippetText;

    @JsonProperty("key")
    private String key;

}
