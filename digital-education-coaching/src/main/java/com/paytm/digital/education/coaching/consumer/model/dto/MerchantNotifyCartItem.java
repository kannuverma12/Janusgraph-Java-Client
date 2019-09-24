package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantNotifyCartItem {

    @NotNull
    private Long itemId;

    @NotNull
    private Long orderId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long productId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String sku;

    @NotNull
    private Long merchantId;

    @NotNull
    private Integer verticalId;

    @NotNull
    private Integer fulfillmentServiceId;

    @NotEmpty
    private String metaData;

    @NotEmpty
    private String fulfillmentReq;

    @NotNull
    private Float price;

    @NotNull
    private Float mrp;

    @NotNull
    private Float convFee;

    @NotNull
    private Float discount;

    @NotNull
    private Integer quantity;

    @NotNull
    private Float sellingPrice;

    @NotEmpty
    private String referenceId;
}