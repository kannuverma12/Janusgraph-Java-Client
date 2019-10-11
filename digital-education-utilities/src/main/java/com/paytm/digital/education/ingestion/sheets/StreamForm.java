package com.paytm.digital.education.ingestion.sheets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.ingestion.annotation.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamForm {

    @JsonProperty("stream_id")
    @GoogleSheetColumnName("Stream Id")
    private Long streamId;

    @JsonProperty("stream_name")
    @GoogleSheetColumnName("Stream Name")
    private String streamName;

    @JsonProperty("logo")
    @GoogleSheetColumnName("Logo")
    private String logo;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;
}
