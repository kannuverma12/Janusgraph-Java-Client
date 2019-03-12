package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CutOff {

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("exam_id")
    private long examId;

    @JsonProperty("exam_start_date")
    private String examStartDate;

    @JsonProperty("exam_end_date")
    private String examEndDate;

}
