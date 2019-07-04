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

    @Field("name")
    @JsonProperty("name")
    private String name;

    @Field("address_line_1")
    @JsonProperty("address_line_1")
    private String addressLine1;

    @Field("address_line_2")
    @JsonProperty("address_line_2")
    private String addressLine2;

    @Field("city")
    @JsonProperty("city")
    private String city;

    @Field("state")
    @JsonProperty("state")
    private String state;

    @Field("pincode")
    @JsonProperty("pincode")
    private String pincode;

    @Field("latitude")
    @JsonProperty("latitude")
    private String latitude;

    @Field("longitude")
    @JsonProperty("longitude")
    private String longitude;

    @Field("email")
    @JsonProperty("email")
    private String email;

    @Field("phone")
    @JsonProperty("phone")
    private String phone;
}
