package com.paytm.digital.education.ingestion.sheets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.ingestion.annotation.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamStreamForm {

    @JsonProperty("exam_full_name")
    @GoogleSheetColumnName("exam full name")
    private String examFullName;

    @JsonProperty("exam_id")
    @GoogleSheetColumnName("exam id")
    private Long examId;

    @JsonProperty("exam_short_name")
    @GoogleSheetColumnName("exam short name")
    private String examShortName;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("merchant_stream")
    @GoogleSheetColumnName("Merchant Stream")
    private String merchantStream;

    @JsonProperty("paytm_stream")
    @GoogleSheetColumnName("Paytm Stream")
    private String paytmStream;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;

}
