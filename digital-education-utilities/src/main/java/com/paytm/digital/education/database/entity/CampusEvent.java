package com.paytm.digital.education.database.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CampusEvent implements Serializable {

    private static final long serialVersionUID = 5296386636462346494L;

    @Field("event_type")
    private String eventType;

    @Field("event_title")
    private String eventTitle;

    @Field("event_description")
    private String eventDescription;

    @Field("submitted_by")
    private String submittedBy;

    @Field("institute_id")
    private Long instituteId;

    @Field("created_at")
    private Date createdAt;

    @Field("images")
    private List<String> images;

    @Field("videos")
    private List<String> videos;

    @Transient
    private List<String> failedMedia;

    @Field("email_address")
    private String emailAddress;
}
