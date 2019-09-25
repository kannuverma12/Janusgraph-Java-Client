package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class AmbassadorRequest {
    @NotBlank
    @JsonProperty("name")
    private String name;

    @Size(min = 10, max = 10)
    @Min(1000000000)
    @JsonProperty("paytm_mobile_number")
    private String paytmMobileNumber;

    @Min(1)
    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("year_&_batch")
    private String yearAndBatch;

    @JsonProperty("course")
    private String course;

    @JsonProperty("timestamp")
    private String timestamp;

    @NotBlank
    @JsonProperty("image")
    private String image;

    @JsonProperty("email_address")
    private String emailAddress;
}
