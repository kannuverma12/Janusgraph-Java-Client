package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficialAddress {

    @Field("city")
    @JsonProperty("city")
    private String city;

    @Field("lat_lon")
    @JsonProperty("lat_lon")
    private String latLon;

    @Field("pin_code")
    @JsonProperty("pin_code")
    private String pinCode;

    @Field("place_id")
    @JsonProperty("place_id")
    private String placeId;

    @Field("state")
    @JsonProperty("state")
    private String state;

    @Field("street_address")
    @JsonProperty("street_address")
    private String streetAddress;
}
