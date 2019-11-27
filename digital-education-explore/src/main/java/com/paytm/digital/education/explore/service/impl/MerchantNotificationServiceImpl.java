package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.MerchantNotification;
import com.paytm.digital.education.explore.database.repository.MerchantNotificationRepository;
import com.paytm.digital.education.explore.request.dto.notification.MerchantNotificationRequest;
import com.paytm.digital.education.explore.response.dto.notification.MerchantNotificationResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MerchantNotificationServiceImpl {

    private MerchantNotificationRepository merchantNotificationRepository;

    public MerchantNotificationResponse saveNotification(
            MerchantNotificationRequest merchantNotificationRequest) {

        MerchantNotificationResponse notificationResponse = new MerchantNotificationResponse();

        MerchantNotification merchantNotification = new MerchantNotification();
        BeanUtils.copyProperties(merchantNotificationRequest, merchantNotification);
        merchantNotification.setMerchantUpdatedAt(merchantNotificationRequest.getUpdatedAt());

        try {
            MerchantNotification dbMerchantNotification =
                    merchantNotificationRepository.save(merchantNotification);
            if (Objects.nonNull(dbMerchantNotification)) {
                notificationResponse.setStatus(200);
                notificationResponse.setMessage("Notification Saved successfully");
                notificationResponse.setMerchantNotification(dbMerchantNotification);
            }
        } catch (Exception e) {
            notificationResponse.setStatus(4012);
            notificationResponse.setMessage("Error saving merchant notification");
        }

        return notificationResponse;
    }
}
