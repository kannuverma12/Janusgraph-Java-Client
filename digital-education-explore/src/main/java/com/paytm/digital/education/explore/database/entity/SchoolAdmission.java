package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.ClassType;
import com.paytm.digital.education.explore.enums.SchoolAdmissionMode;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolAdmission {
    @Field("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("start_date")
    private Date startDate;

    @Field("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("end_date")
    private Date endDate;

    @Field("modes")
    private List<SchoolAdmissionMode> modes;

    @Field("date_type")
    @JsonProperty("date_type")
    private String dateType;

    @Field("class_from")
    @JsonProperty("class_from")
    private ClassType classFrom;

    @Field("class_to")
    @JsonProperty("class_to")
    private ClassType classTo;
}
