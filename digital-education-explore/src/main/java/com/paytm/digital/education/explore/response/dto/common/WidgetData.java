package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.dto.OfficialAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetData {

    @JsonProperty("entity_id")
    private long entityId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("url_display_key")
    private String urlDisplayKey;

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @JsonProperty("stream")
    private String stream;

    @JsonProperty("stream_id")
    private Long streamId;

    @JsonProperty("stream_display_name")
    private String streamDisplay;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("important_dates")
    private Map<String, String> importantDates;
}
