package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormSubmissionDate {

    @Field("date_range_start")
    @JsonProperty("date_range_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateRangeStart;

    @Field("date_range_end")
    @JsonProperty("date_range_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateRangeEnd;

    @Field("date")
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
}
