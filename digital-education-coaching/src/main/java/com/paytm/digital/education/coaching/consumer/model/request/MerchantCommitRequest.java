package com.paytm.digital.education.coaching.consumer.model.request;

import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantCommitOrderInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantCommitPaymentInfo;
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
public class MerchantCommitRequest {

    private String                        paytmOrderId;
    private String                        merchantId;
    private MerchantCommitUserInfo        userInfo;
    private List<MerchantCommitOrderInfo> orderInfo;
    private MerchantCommitPaymentInfo     paymentInfo;
    private Long                          timestamp;
}
