package com.paytm.digital.education.ingestion.sheets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.ingestion.annotation.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamStreamForm {

    @JsonProperty("exam_full_name")
    @GoogleSheetColumnName("Exam Full Name")
    private String examFullName;

    @JsonProperty("exam_id")
    @GoogleSheetColumnName("Exam Id")
    private Long examId;

    @JsonProperty("exam_short_name")
    @GoogleSheetColumnName("Exam Short Name")
    private String examShortName;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("domains")
    @GoogleSheetColumnName("Domains")
    private String domains;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;

}
