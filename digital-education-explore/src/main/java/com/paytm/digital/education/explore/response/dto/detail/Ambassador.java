package com.paytm.digital.education.explore.response.dto.detail;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ambassador {
    @JsonProperty("name")
    private String name;

    @JsonProperty("paytm_mobile_number")
    private String paytmMobileNumber;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("year_and_batch")
    private String yearAndBatch;

    @JsonProperty("course")
    private String course;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("score")
    private Double score;
}
