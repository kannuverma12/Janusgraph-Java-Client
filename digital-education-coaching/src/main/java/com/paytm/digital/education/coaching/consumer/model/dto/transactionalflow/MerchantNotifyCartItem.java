package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantNotifyCartItem {

    @NotNull
    @ApiModelProperty(required = true)
    private Long itemId;

    @NotNull
    @ApiModelProperty(required = true)
    private Long orderId;

    @NotNull
    @ApiModelProperty(required = true)
    private Long customerId;

    @NotNull
    @ApiModelProperty(required = true)
    private Long productId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String name;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String sku;

    @NotNull
    @ApiModelProperty(required = true)
    private Long merchantId;

    private Integer verticalId;
    private Integer fulfillmentServiceId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String metaData;

    private String fulfillmentReq;

    @NotNull
    @ApiModelProperty(required = true)
    private Float price;

    @NotNull
    @ApiModelProperty(required = true)
    private Float mrp;

    @NotNull
    @ApiModelProperty(required = true)
    private Float convFee;

    @NotNull
    @ApiModelProperty(required = true)
    private Float discount;

    @NotNull
    @ApiModelProperty(required = true)
    private Integer quantity;

    @NotNull
    @ApiModelProperty(required = true)
    private Float totalSellingPrice;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String referenceId;
}
