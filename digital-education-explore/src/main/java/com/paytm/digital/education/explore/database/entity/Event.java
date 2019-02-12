package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    @Field("month")
    @JsonProperty("month")
    private String month;

    @Field("eventId")
    @JsonProperty("event_id")
    private long eventId;

    @Field("modes")
    @JsonProperty("modes")
    private List<String> modes;

    @Field("type")
    @JsonProperty("type")
    private String type;

    @Field("otherEventLabel")
    @JsonProperty("other_event_label")
    private String otherEventLabel;
}
