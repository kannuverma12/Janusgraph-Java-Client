package com.paytm.digital.education.explore.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document(collection = "failed_campus_ambassador")
public class FailedCampusAmbassador {
    @Field("name")
    private String name;

    @Field("paytm_mobile_number")
    private String paytmMobileNumber;

    @Field("institute_id")
    private Long instituteId;

    @Field("year_and_batch")
    private String yearAndBatch;

    @Field("course")
    private String course;

    @Field("timestamp")
    private Date timestamp;

    @Field("failed_date")
    private Date failedDate;

    @Field("image_url")
    private String imageUrl;

    @Field("email_address")
    private String emailAddress;

    @Field("has_imported")
    private Boolean hasImported = false;

    @Field("reason")
    private String reason;

}
