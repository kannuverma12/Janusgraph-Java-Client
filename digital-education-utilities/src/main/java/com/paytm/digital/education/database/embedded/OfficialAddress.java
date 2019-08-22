package com.paytm.digital.education.database.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficialAddress {

    @Field("address_line1")
    @JsonProperty("address_line1")
    private String addressLine1;

    @Field("address_line2")
    @JsonProperty("address_line2")
    private String addressLine2;

    @Field("address_line3")
    @JsonProperty("address_line3")
    private String addressLine3;

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
