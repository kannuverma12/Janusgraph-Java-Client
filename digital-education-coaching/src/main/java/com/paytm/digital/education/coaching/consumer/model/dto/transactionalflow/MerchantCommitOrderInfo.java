package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantCommitOrderInfo {

    private String                paytmProductId;
    private String                merchantProductId;
    private String                paytmOrderItemId;
    private String                merchantPromoCode;
    private Double                price;
    private Double                discount;
    private Double                totalPrice;
    private String                productName;
    private String                productDescription;
    private Double                quantity;
    private MerchantCommitTaxInfo taxInfo;
}
