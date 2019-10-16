package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantCommitOrderInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantCommitUserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantCommitRequest {

    private String                        paytmOrderId;
    private String                        merchantId;
    private MerchantCommitUserInfo        userInfo;
    private List<MerchantCommitOrderInfo> orderInfo;
    private String                        orderCreatedAt;
}
