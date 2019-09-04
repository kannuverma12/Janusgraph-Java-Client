package com.paytm.digital.education.coaching.producer.model.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OfficialAddress {

    @NotEmpty
    @Size(max = 200)
    @ApiModelProperty(value = "description about coaching institute")
    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    @NotEmpty
    @Size(max = 20)
    private String city;

    @NotEmpty
    @Size(max = 20)
    private String state;

    @NotEmpty
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[0-9]*$")
    private String pincode;

    @NotEmpty
    //TODO : add validation integer between -90 and +90 and decimal upto 6 places
    private Double latitude;

    @NotEmpty
    //TODO : add validation integer between -180 and +180 and decimal upto 6 places
    private Double longitude;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[0-9]*$")
    private String phone;
}
