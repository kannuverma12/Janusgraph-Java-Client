package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.MerchantCommitOrderInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantCommitPaymentInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantCommitTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantCommitUserInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.MetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyUserInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantCommitRequest;
import com.paytm.digital.education.coaching.consumer.model.response.MerchantNotifyResponse;
import com.paytm.digital.education.coaching.utils.AuthUtils;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.RestTemplateConstants.PAYTM_HOST_FOR_SIGNATURE;

@Slf4j
@Service
@AllArgsConstructor
public class MerchantCallImpl implements MerchantCall {

    @Override
    public MerchantCommitRequest getMerchantCommitRequestBody(
            List<MerchantNotifyCartItem> cartItems, NotifyUserInfo userInfo) {
        long currentTimestamp = System.currentTimeMillis();
        MerchantCommitRequest merchantCommitRequest = MerchantCommitRequest
                .builder()
                .paytmOrderId(String.valueOf(cartItems.get(0).getOrderId()))
                .merchantId(String.valueOf(cartItems.get(0).getMerchantId()))
                .timestamp(currentTimestamp)
                .build();

        List<MerchantCommitOrderInfo> orderItems = new ArrayList<>();
        float totalPrice = 0;

        for (MerchantNotifyCartItem cartItem : cartItems) {
            MetaData metaData = JsonUtils.fromJson(cartItem.getMetaData(), MetaData.class);

            if (Objects.isNull(metaData) || Objects.isNull(metaData.getTaxInfo())) {
                throw new BadRequestException(ErrorEnum.INVALID_CART_ITEMS);
            }

            totalPrice += cartItem.getPrice();

            MerchantCommitTaxInfo taxInfo = MerchantCommitTaxInfo
                    .builder()
                    .cgst((double) metaData.getTaxInfo().getTotalCGST())
                    .sgst((double) metaData.getTaxInfo().getTotalSGST())
                    .igst((double) metaData.getTaxInfo().getTotalIGST())
                    .utgst((double) metaData.getTaxInfo().getTotalUTGST())
                    .total((double) (metaData.getTaxInfo().getTotalCGST() + metaData.getTaxInfo()
                            .getTotalSGST() + metaData.getTaxInfo().getTotalIGST() + metaData
                            .getTaxInfo().getTotalUTGST()))
                    .build();

            orderItems.add(MerchantCommitOrderInfo
                    .builder()
                    .paytmProductId(String.valueOf(cartItem.getProductId()))
                    .paytmOrderItemId(String.valueOf(cartItem.getItemId()))
                    .price((double) cartItem.getPrice())
                    .discount((double) cartItem.getDiscount())
                    .totalPrice((double) cartItem.getSellingPrice())
                    .quantity((double) cartItem.getQuantity())
                    .taxInfo(taxInfo)
                    .build());
        }

        MerchantCommitPaymentInfo paymentInfo = MerchantCommitPaymentInfo
                .builder()
                .totalPayoutAmount((double) totalPrice)
                .paymentTimestamp(currentTimestamp)
                .build();

        MerchantCommitUserInfo commitUserInfo = MerchantCommitUserInfo
                .builder()
                .userMobile(userInfo.getPhone())
                .userEmail(userInfo.getEmail())
                .userName(userInfo.getFirstName() + " " + userInfo.getLastName())
                .build();

        merchantCommitRequest.setUserInfo(commitUserInfo);
        merchantCommitRequest.setPaymentInfo(paymentInfo);
        merchantCommitRequest.setOrderInfo(orderItems);
        return merchantCommitRequest;
    }

    @Override
    public MerchantNotifyResponse commitMerchantOrder(MerchantCommitRequest request,
            NotifyMerchantInfo merchantInfo) {
        String endpoint = merchantInfo.getEndPoint();
        String completeEndpoint = merchantInfo.getHost() + endpoint;
        String method = "POST";
        Map<String, Object> queryParams = new TreeMap<>();
        String queryParamsString = "";

        if (!CollectionUtils.isEmpty(queryParams)) {
            queryParamsString = JsonUtils.toJson(queryParams);
        }

        String signatureMessage =
                PAYTM_HOST_FOR_SIGNATURE + "|" + endpoint + "|" + method + "|" + JsonUtils
                        .toJson(request) + "|" + queryParamsString + "|" + request.getTimestamp();

        String signature = AuthUtils.getSignature(signatureMessage, merchantInfo.getSecretKey());

        /*try {
            return CoachingRestTemplate.getRequestTemplate(MERCHANT_COMMIT_TIMEOUT_MS)
                    .postForObject(
                            UriComponentsBuilder.fromHttpUrl(completeEndpoint).toUriString(),
                            HeaderTemplate.getMerchantHeader(MDC.get(PAYTM_REQUEST_ID), signature,
                            merchantInfo.getAccessKey()),
                            request,
                            MerchantNotifyResponse.class, queryParams);
        } catch (Exception e) {
            log.error("Exception occurred in merchant commit call for body: {} and exception: ",
                    request, e);
        }*/
        return null;
    }
}
