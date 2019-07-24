package com.paytm.digital.education.explore.xcel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class XcelEvent {
    @Field("event_type")
    @JsonProperty("event_type")
    private String eventType;

    @Field("event_title")
    @JsonProperty("event_title")
    private String eventTitle;

    @Field("event_description")
    @JsonProperty("event_description")
    private String eventDescription;

    @Field("submitted_by_:_institute_paytm_phone_number")
    @JsonProperty("submitted_by_:_institute_paytm_phone_number")
    private String submittedBy;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("timestamp")
    private String timestamp;

    @Field("event_media")
    @JsonProperty("event_media")
    private String eventMedia;

    @Field("email_address")
    @JsonProperty("email_address")
    private String emailAddress;
}
