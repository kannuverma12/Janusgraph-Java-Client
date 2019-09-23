package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyUserInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantCommitRequest;
import com.paytm.digital.education.coaching.consumer.model.response.MerchantNotifyResponse;

import java.util.List;

public interface MerchantCall {

    MerchantCommitRequest getMerchantCommitRequestBody(List<MerchantNotifyCartItem> cartItems,
            NotifyUserInfo userInfo);

    MerchantNotifyResponse commitMerchantOrder(MerchantCommitRequest request,
            NotifyMerchantInfo merchantInfo);
}
