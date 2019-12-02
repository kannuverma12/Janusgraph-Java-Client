package com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseFee {

    private String header;
    private String currency;
    private Float  originalPrice;
    private Float  discountedPrice;
    private String discountPercentage;

    private List<TaxBreakupInfo> taxInfo;
    private Float                totalTax;

    private Float                convFee;
    private List<TaxBreakupInfo> convFeeTaxInfo;
    private Float                totalConvFeeTax;
}
