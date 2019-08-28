package com.paytm.digital.education.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class CampusAmbassador {
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

    @Field("created_at")
    private Date createdAt;

    @Field("last_updated")
    private Date lastUpdated;

    @Field("image_url")
    private String imageUrl;

    @Field("email_address")
    private String emailAddress;

    @Field("score")
    private Double score;
}
