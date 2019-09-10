package com.paytm.digital.education.coaching.producer.model.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Min(value = -90)
    @Max(value = 90)
    private Double latitude;

    @NotNull
    @Min(value = -180)
    @Max(value = 180)
    private Double longitude;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[0-9]*$")
    private String phone;
}
