package com.paytm.digital.education.coaching.consumer.model.response.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.es.model.GeoLocation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCenterData extends SearchBaseData {

    private Long        centerId;
    private Long        instituteId;
    private String      officialName;
    private String      imageUrl;
    private String      openingTime;
    private String      closingTime;
    private String      addressLine1;
    private String      addressLine2;
    private String      addressLine3;
    private String      city;
    private String      state;
    private String      pincode;
    private String      email;
    private String      phone;
    private String      distance;
    private GeoLocation location;
}
