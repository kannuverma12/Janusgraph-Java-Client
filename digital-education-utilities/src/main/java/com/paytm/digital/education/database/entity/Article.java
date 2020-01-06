package com.paytm.digital.education.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
public class Article implements Serializable {

    private static final long serialVersionUID = -7687616076765113553L;

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

    @Field("created_at")
    private Date createdAt;

    @Field("article_pdf")
    private String articlePdf;

    @Field("email_address")
    private String emailAddress;
}
