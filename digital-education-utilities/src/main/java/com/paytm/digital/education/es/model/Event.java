package com.paytm.digital.education.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    @JsonProperty("event_id")
    private Integer eventId;

    @JsonProperty("month")
    private String month;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("date_range_start")
    private Date startDate;

    @JsonProperty("date_range_end")
    private Date endDate;

    @JsonProperty("type")
    private String type;

    @JsonProperty("certainty")
    private String certainty;

    @JsonProperty("date_name")
    private String dateName;
}
