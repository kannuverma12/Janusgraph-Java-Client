package com.paytm.digital.education.database.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OfficialAddress {

    @Field("address_line1")
    private String addressLine1;

    @Field("address_line2")
    private String addressLine2;

    @Field("address_line3")
    private String addressLine3;

    @Field("city")
    private String city;

    @Field("state")
    private String state;

    @Field("pincode")
    private String pincode;

    @Field("latitude")
    private Double latitude;

    @Field("longitude")
    private Double longitude;

    @Field("email")
    private String email;

    @Field("phone")
    private String phone;
}
