package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamSubItemData {
    @JsonProperty("exam_id")
    private int examId;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("name")
    private String examShortName;

    @JsonProperty("full_name")
    private String officialName;

    @JsonProperty("logo")
    private String logoUrl;

}
