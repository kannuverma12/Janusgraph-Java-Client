package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "failed_event")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FailedEvent {
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

    @Field("timestamp")
    private Date timestamp;

    @Field("failed_date")
    private Date failedDate;

    @Field("failed_media")
    private List<String> failedMedia;

    @Field("images")
    private List<String> images;

    @Field("videos")
    private List<String> videos;

    @Field("has_imported")
    private Boolean hasImported = false;

    @Field("reason")
    private String reason;
}
