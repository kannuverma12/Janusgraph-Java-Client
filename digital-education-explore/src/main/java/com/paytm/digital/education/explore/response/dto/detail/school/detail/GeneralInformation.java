package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralInformation {
    @JsonProperty("street_address")
    private String streetAddress;
    private String phone;
    private String email;

    @JsonProperty("lat_lon")
    private String latLon;

    @JsonProperty("official_website_link")
    private String officialWebsiteLink;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;
}
