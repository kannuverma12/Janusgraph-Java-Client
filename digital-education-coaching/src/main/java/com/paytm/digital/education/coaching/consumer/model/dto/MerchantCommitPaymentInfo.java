package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCommitPaymentInfo {

    private Double totalPayoutAmount;
    private Long   paymentTimestamp;
}
