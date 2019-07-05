package com.paytm.digital.education.coaching.googlesheet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoachingCentreForm {
    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("center_id")
    private Long centerId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("street_address_1")
    private String streetAddress1;

    @JsonProperty("street_address_2")
    private String streetAddress2;

    @JsonProperty("street_address_3")
    private String streetAddress3;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("pin_code")
    private Integer pinCode;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("courses_available")
    private String coursesAvailable;

    @JsonProperty("status")
    private String status;
}
