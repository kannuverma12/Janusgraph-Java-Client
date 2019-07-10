package com.paytm.digital.education.explore.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDto {

    @JsonProperty("month")
    private String monthDate;

    @JsonProperty("event_id")
    private long eventId;

    @JsonProperty("modes")
    private List<String> modes;

    @JsonProperty("type")
    private String type;

    @JsonProperty("other_event_label")
    private String otherEventLabel;

    @JsonProperty("certainty")
    private String certainty;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date")
    private Date date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_range_start")
    private Date dateRangeStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_range_end")
    private Date dateRangeEnd;

}
