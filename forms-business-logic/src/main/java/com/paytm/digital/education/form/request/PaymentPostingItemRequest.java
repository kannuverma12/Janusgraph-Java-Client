package com.paytm.digital.education.form.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentPostingItemRequest {

    @JsonProperty("id")
    @NotNull
    private Long itemId;

    @JsonProperty("order_id")
    @NotNull
    private Long orderId;

    @JsonProperty("product_id")
    @NotNull
    private Long productId;

    @JsonProperty("merchant_id")
    @NotNull
    private Long merchantId;

    @JsonProperty("vertical_id")
    @NotNull
    private Integer verticalId;

    @JsonProperty("fulfillment_service")
    @NotNull
    private Integer fulfillmentService;

    @NotEmpty
    @JsonProperty("meta_data")
    private String metaData;

    @JsonProperty("price")
    @NotNull
    private Float price;

}
