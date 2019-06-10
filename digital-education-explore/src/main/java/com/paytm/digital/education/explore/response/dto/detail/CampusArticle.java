package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampusArticle {
    @JsonProperty("article_title")
    private String articleTitle;

    @JsonProperty("article_short_description")
    private String articleShortDescription;

    @JsonProperty("submitted_by")
    private String submittedBy;

    @JsonProperty("submitter_designation")
    private String submitterDesignation;

    @JsonProperty("submitter_image_url")
    private String submitterImageUrl;

    @JsonProperty("student_paytm_mobile_number")
    private String studentPaytmMobileNumber;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("submitted_date")
    private Date submittedDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("article_pdf")
    private String articlePdf;
}
