package com.paytm.digital.education.explore.response.dto.search;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamData extends SearchBaseData {

    @JsonProperty("exam_id")
    private int          examId;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("official_name")
    private String       officialName;

    @JsonProperty("application_month")
    private String       applicationMonth;

    @JsonProperty("result_month")
    private String       resultMonth;

    @JsonProperty("exam_month")
    private String       examMonth;

    @JsonProperty("result_start_date")
    private String       resultStartDate;

    @JsonProperty("result_end_date")
    private String       resultEndDate;

    @JsonProperty("application_start_date")
    private String       applicationStartDate;

    @JsonProperty("application_end_date")
    private String       applicationEndDate;

    @JsonProperty("exam_start_date")
    private String       examStartDate;

    @JsonProperty("exam_end_date")
    private String       examEndDate;

    @JsonProperty("logo_url")
    private String       logoUrl;

    @JsonProperty("data_available")
    private List<String> dataAvailable;

}
