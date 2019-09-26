package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
