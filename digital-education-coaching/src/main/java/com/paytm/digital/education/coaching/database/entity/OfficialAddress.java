package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficialAddress {

    @Field("street_address")
    @JsonProperty("street_address")
    private String streetAddress;

    @Field("city")
    @JsonProperty("city")
    private String city;

    @Field("state")
    @JsonProperty("state")
    private String state;

    @Field("pincode")
    @JsonProperty("pincode")
    private Integer pincode;

    @Field("lat_lon")
    @JsonProperty("lat_lon")
    private String latLon;

    @Field("email")
    @JsonProperty("email")
    private String email;

    @Field("phone")
    @JsonProperty("phone")
    private String phone;
}
