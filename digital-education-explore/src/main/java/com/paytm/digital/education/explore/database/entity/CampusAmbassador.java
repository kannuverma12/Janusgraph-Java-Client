package com.paytm.digital.education.explore.database.entity;

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
    private String createdAt;

    @Field("image_url")
    private String imageUrl;
}
