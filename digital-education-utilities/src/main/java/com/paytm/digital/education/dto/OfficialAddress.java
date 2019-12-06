package com.paytm.digital.education.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficialAddress implements Serializable {

    private static final long serialVersionUID = 885668139659643283L;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("lat_lon")
    private String latLon;

    @JsonProperty("street_address")
    private String streetAddress;

    @JsonProperty("pin_code")
    private String pinCode;

    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("url")
    private String url;

}
