package com.paytm.digital.education.coaching.consumer.service.transactionalflow;

import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyUserInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantCommitRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.MerchantNotifyResponse;

import java.util.List;

public interface MerchantCall {

    MerchantCommitRequest getMerchantCommitRequestBody(List<MerchantNotifyCartItem> cartItems,
            NotifyUserInfo userInfo);

    MerchantNotifyResponse commitMerchantOrder(MerchantCommitRequest request,
            NotifyMerchantInfo merchantInfo);
}
