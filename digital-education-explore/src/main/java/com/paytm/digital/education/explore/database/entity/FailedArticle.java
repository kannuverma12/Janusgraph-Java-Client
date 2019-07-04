package com.paytm.digital.education.explore.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "failed_article")
public class FailedArticle {
    @Field("article_title")
    private String articleTitle;

    @Field("article_short_description")
    private String articleShortDescription;

    @Field("submitted_by")
    private String submittedBy;

    @Field("student_paytm_mobile_number")
    private String studentPaytmMobileNumber;

    @Field("institute_id")
    private Long instituteId;

    @Field("submitted_date")
    private Date submittedDate;

    @Field("timestamp")
    private Date timestamp;

    @Field("failed_date")
    private Date failedDate;

    @Field("article_pdf")
    private String articlePdf;

    @Field("email_address")
    private String emailAddress;

    @Field("has_imported")
    private Boolean hasImported = false;

    @Field("reason")
    private String reason;
}
