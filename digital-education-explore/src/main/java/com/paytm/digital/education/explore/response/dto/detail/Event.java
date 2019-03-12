package com.paytm.digital.education.explore.response.dto.detail;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    @JsonProperty("name")
    private String       name;

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date         date;

    @JsonProperty("date_start_range")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date         dateStartRange;

    @JsonProperty("date_end_range")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date         dateEndRange;

    @JsonProperty("type")
    private String       type;

    @JsonProperty("mode")
    private List<String> modes;

    @JsonProperty("month")
    private String       monthDate;

    @JsonProperty("certainity")
    private String       certainity;

}
