package com.paytm.digital.education.coaching.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCenterSearch {

    private Long         centerId;
    private Long         instituteId;
    private String       officialName;
    private String       centerImage;
    private String       openingTime;
    private String       closingTime;
    private String       addressLine1;
    private String       addressLine2;
    private String       addressLine3;
    private String       city;
    private String       state;
    private String       pincode;
    private String       email;
    private String       phone;
    private GeoLocation  location;
    private List<Double> sort;
}
