package com.paytm.digital.education.explore.database.entity;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    @Field("month")
    private String       monthDate;

    @Field("eventId")
    private long         eventId;

    @Field("modes")
    private List<String> modes;

    @Field("type")
    private String       type;

    @Field("other_event_label")
    private String       otherEventLabel;

    @Field("certainty")
    @JsonProperty("certainty")
    private String       certainty;

    @Field("date")
    @JsonProperty("date")
    private Date         date;

    @Field("date_range_start")
    @JsonProperty("date_range_start")
    private Date         dateRangeStart;

    @Field("date_range_end")
    @JsonProperty("date_range_end")
    private Date         dateRangeEnd;

}
