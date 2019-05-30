package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampusArticle {
    @JsonProperty("article_title")
    private String articleTitle;

    @JsonProperty("article_short_description")
    private String articleShortDescription;

    @JsonProperty("submitted_by")
    private String submittedBy;

    @JsonProperty("student_paytm_mobile_number")
    private String studentPaytmMobileNumber;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("submitted_date")
    private String submittedDate;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("article_pdf")
    private String articlePdf;
}
