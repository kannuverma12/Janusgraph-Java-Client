package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCommitTaxInfo {

    private Double CGST;
    private Double SGST;
    private Double IGST;
    private Double UTGST;
    private Double total;
}
