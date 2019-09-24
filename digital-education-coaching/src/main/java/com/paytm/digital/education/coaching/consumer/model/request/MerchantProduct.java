package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantProduct {

    @NotNull
    private Long productId;

    @NotNull
    private String productName;

    private String description;

    @NotNull
    private Float price;

    @Valid
    private MerchantProductTaxData merchantProductTaxData;

}
