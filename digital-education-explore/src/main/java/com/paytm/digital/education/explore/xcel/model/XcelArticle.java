package com.paytm.digital.education.explore.xcel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class XcelArticle {
    @JsonProperty("article_title")
    private String articleTitle;

    @JsonProperty("article_short_description")
    private String articleShortDescription;

    @JsonProperty("submitted_by")
    private String submittedBy;

    @JsonProperty("student_paytm_mobile_number")
    private String studentPaytmMobileNumber;

    @JsonProperty("institute_id")
    private String instituteId;

    @JsonProperty("submitted_date")
    private String submittedDate;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("article_pdf")
    private String articlePdf;
}