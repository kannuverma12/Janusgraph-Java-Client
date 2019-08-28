package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamImportantDate {

    @JsonProperty("name")
    private String name;

    @JsonProperty("date_start_range")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date dateStartRange;

    @JsonProperty("date_end_range")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date dateEndRange;

    @JsonProperty("date_start_range_timestamp")
    private Date dateStartRangeTimestamp;

    @JsonProperty("date_end_range_timestamp")
    private Date dateEndRangeTimestamp;

    @JsonProperty("type")
    private String type;

    @JsonProperty("type_display_name")
    private String typeDisplayName;

    @JsonProperty("mode")
    private List<String> modes;

    @JsonProperty("month")
    private String monthDate;

    @JsonProperty("month_timestamp")
    private Date monthTimestamp;

    @JsonProperty("certainity")
    private String certainity;
}
