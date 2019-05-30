package com.paytm.digital.education.explore.response.dto.detail;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("image_url")
    private String imageUrl;
}
