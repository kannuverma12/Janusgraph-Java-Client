package com.paytm.digital.education.coaching.googlesheet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingFacilityForm {
    @JsonProperty("enter_institute_id")
    private Long instituteId;

    @JsonProperty("select_facility_type")
    private String facilityType;

    @JsonProperty("facility_description")
    private String facilityDescription;
}
