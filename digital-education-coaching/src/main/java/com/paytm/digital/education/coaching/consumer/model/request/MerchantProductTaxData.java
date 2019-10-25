package com.paytm.digital.education.coaching.consumer.model.request;

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
public class MerchantProductTaxData {

    private Float  totalCGST;
    private Float  totalSGST;
    private Float  totalIGST;
    private Float  totalUTGST;
    private String gstin;
}
