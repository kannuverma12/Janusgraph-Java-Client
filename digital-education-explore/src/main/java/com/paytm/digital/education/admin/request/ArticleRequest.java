package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class ArticleRequest {

    @NotBlank
    @JsonProperty("article_title")
    private String articleTitle;

    @NotBlank
    @JsonProperty("article_short_description")
    private String articleShortDescription;

    @NotBlank
    @JsonProperty("submitted_by")
    private String submittedBy;

    @Size(min = 10, max = 10, message = "Enter 10 digits mobile number")
    @Min(value = 1000000000, message = "Enter 10 digits mobile number")
    @JsonProperty("student_paytm_mobile_number")
    private String studentPaytmMobileNumber;

    @Min(1)
    @JsonProperty("institute_id")
    private Long instituteId;

    @NotBlank
    @JsonProperty("submitted_date")
    private String submittedDate;

    @JsonProperty("timestamp")
    private String timestamp;

    @NotBlank
    @JsonProperty("article_pdf")
    private String articlePdf;

    @JsonProperty("email_address")
    private String emailAddress;
}

