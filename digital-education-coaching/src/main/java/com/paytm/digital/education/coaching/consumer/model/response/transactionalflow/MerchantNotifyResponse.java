package com.paytm.digital.education.coaching.consumer.model.response.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantOrderData;
import com.paytm.digital.education.coaching.enums.MerchantNotifyFailureReason;
import com.paytm.digital.education.coaching.enums.MerchantNotifyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantNotifyResponse {

    private MerchantNotifyStatus        status;
    private MerchantNotifyFailureReason failureReason;
    private MerchantOrderData           merchantResponse;
}
