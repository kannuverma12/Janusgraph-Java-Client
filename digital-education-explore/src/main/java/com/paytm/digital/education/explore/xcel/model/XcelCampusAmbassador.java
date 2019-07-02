package com.paytm.digital.education.explore.xcel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class XcelCampusAmbassador {
    @JsonProperty("name")
    private String name;

    @JsonProperty("paytm_mobile_number")
    @Field("paytm_mobile_number")
    private String paytmMobileNumber;

    @JsonProperty("institute_id")
    @Field("institute_id")
    private Long instituteId;

    @JsonProperty("year_&_batch")
    @Field("year_&_batch")
    private String yearAndBatch;

    @JsonProperty("course")
    private String course;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("image")
    private String image;

    @JsonProperty("email_address")
    @Field("email_address")
    private String emailAddress;
}
