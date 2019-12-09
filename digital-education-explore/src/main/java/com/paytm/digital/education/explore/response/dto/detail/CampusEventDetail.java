package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampusEventDetail implements Serializable {

    private static final long serialVersionUID = 2033625092172304947L;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("event_title")
    private String eventTitle;

    @JsonProperty("event_description")
    private String eventDescription;

    @JsonProperty("submitted_by")
    private String submittedBy;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("images")
    private List<String> images;

    @JsonProperty("videos")
    private List<String> videos;
}

