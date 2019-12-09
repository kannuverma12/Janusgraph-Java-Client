package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolOfficialAddress implements Serializable {

    private static final long serialVersionUID = -4715217337527793707L;

    @Field("city")
    @JsonProperty("city")
    private String city;

    @Field("lat_lon")
    @JsonProperty("lat_lon")
    private String latLon;

    @Field("pincode")
    @JsonProperty("pincode")
    private String pinCode;

    @Field("placeId")
    @JsonProperty("place_id")
    private String placeId;

    @Field("state")
    @JsonProperty("state")
    private String state;

    @Field("street_address")
    @JsonProperty("street_address")
    private String streetAddress;

    @Field("district")
    @JsonProperty("district")
    private String district;
}
