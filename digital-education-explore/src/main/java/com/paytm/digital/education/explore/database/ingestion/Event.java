package com.paytm.digital.education.explore.database.ingestion;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    @Field("month")
    @JsonProperty("month")
    private String monthDate;

    @Field("event_id")
    @JsonProperty("event_id")
    private long eventId;

    @Field("modes")
    @JsonProperty("modes")
    private List<String> modes;

    @Field("type")
    @JsonProperty("type")
    private String type;

    @Field("other_event_label")
    @JsonProperty("other_event_label")
    private String otherEventLabel;

    @Field("certainty")
    @JsonProperty("certainty")
    private String certainty;

    @Field("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date")
    private Date date;

    @Field("date_range_start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_range_start")
    private Date dateRangeStart;

    @Field("date_range_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("date_range_end")
    private Date dateRangeEnd;

    @Field("date_name")
    @JsonProperty("date_name")
    private String dateName;
}
