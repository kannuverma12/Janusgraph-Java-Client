package com.paytm.digital.education.explore.xcel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class XcelArticle {
    @Field("article_title")
    @JsonProperty("article_title")
    private String articleTitle;

    @Field("article_short_description")
    @JsonProperty("article_short_description")
    private String articleShortDescription;

    @Field("submitted_by")
    @JsonProperty("submitted_by")
    private String submittedBy;

    @Field("student_paytm_mobile_number")
    @JsonProperty("student_paytm_mobile_number")
    private String studentPaytmMobileNumber;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("submitted_date")
    @JsonProperty("submitted_date")
    private String submittedDate;

    @JsonProperty("timestamp")
    private String timestamp;

    @Field("article_pdf")
    @JsonProperty("article_pdf")
    private String articlePdf;

    @Field("email_address")
    @JsonProperty("email_address")
    private String emailAddress;
}
