package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCenterForm {

    private Long   centerId;
    private Long   instituteId;
    private String officialName;
    private String courseTypes;

    private String streetAddress1;
    private String streetAddress2;
    private String streetAddress3;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private String emailId;
    private String phoneNumber;

    private Integer globalPriority;
    private String  statusActive;
}
