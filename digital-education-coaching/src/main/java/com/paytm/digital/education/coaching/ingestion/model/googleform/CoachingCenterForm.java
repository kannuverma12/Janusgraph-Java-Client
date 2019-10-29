package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCenterForm {

    @JsonProperty("center_id")
    @GoogleSheetColumnName("Center Id")
    private Long centerId;

    @JsonProperty("institute_id")
    @GoogleSheetColumnName("Institute Id")
    private Long instituteId;

    @JsonProperty("official_name")
    @GoogleSheetColumnName("Official Name")
    private String officialName;

    @JsonProperty("course_types")
    @GoogleSheetColumnName("Course Types")
    private String courseTypes;

    @JsonProperty("street_address1")
    @GoogleSheetColumnName("Street Address1")
    private String streetAddress1;

    @JsonProperty("street_address2")
    @GoogleSheetColumnName("Street Address2")
    private String streetAddress2;

    @JsonProperty("street_address3")
    @GoogleSheetColumnName("Street Address3")
    private String streetAddress3;

    @JsonProperty("city")
    @GoogleSheetColumnName("City")
    private String city;

    @JsonProperty("state")
    @GoogleSheetColumnName("State")
    private String state;

    @JsonProperty("pincode")
    @GoogleSheetColumnName("Pincode")
    private String pincode;

    @JsonProperty("latitude")
    @GoogleSheetColumnName("Latitude")
    private Double latitude;

    @JsonProperty("longitude")
    @GoogleSheetColumnName("Longitude")
    private Double longitude;

    @JsonProperty("email_id")
    @GoogleSheetColumnName("Email Id")
    private String emailId;

    @JsonProperty("phone_number")
    @GoogleSheetColumnName("Phone Number")
    private String phoneNumber;

    @JsonProperty("opening_time")
    @GoogleSheetColumnName("Opening Time")
    private String openingTime;

    @JsonProperty("closing_time")
    @GoogleSheetColumnName("Closing Time")
    private String closingTime;

    @JsonProperty("center_image")
    @GoogleSheetColumnName("Center Image")
    private String centerImage;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;
}
