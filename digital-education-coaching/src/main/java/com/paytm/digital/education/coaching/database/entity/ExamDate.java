package com.paytm.digital.education.coaching.database.entity;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM_DD;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamDate {

    @Field("start_date")
    @JsonProperty("start_date")
    @JsonFormat(pattern = YYYY_MM_DD)
    private Date dateStart;

    @Field("end_date")
    @JsonProperty("end_date")
    @JsonFormat(pattern = YYYY_MM_DD)
    private Date dateEnd;

    @Field("start_time")
    @JsonProperty("start_time")
    private String startTime;

    @Field("end_time")
    @JsonProperty("end_time")
    private String endTime;

}
