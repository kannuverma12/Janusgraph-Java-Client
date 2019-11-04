package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.enums.ExamSectionType;
import lombok.Data;

@Data
public class SectionConfiguration {

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("short_text")
    private String shortText;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("type")
    private ExamSectionType type;

}
