package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCommitTaxInfo {

    private Double cgst;
    private Double sgst;
    private Double igst;
    private Double utgst;
    private Double total;
}
