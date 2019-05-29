package com.paytm.digital.education.explore.xcel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class XcelEvent {
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("event_title")
    private String eventTitle;

    @JsonProperty("event_description")
    private String eventDescription;

    @JsonProperty("submitted_by_:_institute_paytm_phone_number")
    private String submittedBy;

    @JsonProperty("institute_id")
    private String instituteId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("event_media")
    private String eventMedia;
}
