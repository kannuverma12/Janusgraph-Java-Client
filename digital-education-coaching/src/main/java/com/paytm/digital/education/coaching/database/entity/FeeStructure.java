package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeeStructure {

    @Field("amount")
    @JsonProperty("amount")
    private Integer amount;

    @Field("frequency")
    @JsonProperty("frequency")
    private Integer frequency;

    @Field("payment_option")
    @JsonProperty("payment_option")
    private String paymentOption;

    @Field("emi_available")
    @JsonProperty("emi_available")
    private Boolean emiAvailable;
}
