package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormSubmissionDateDto {

    @JsonProperty("date_range_start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateRangeStart;

    @JsonProperty("date_range_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateRangeEnd;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
