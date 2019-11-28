package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.MerchantNotification;
import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.database.repository.MerchantStreamRepository;
import com.paytm.digital.education.explore.database.repository.MerchantNotificationRepository;
import com.paytm.digital.education.explore.request.dto.notification.MerchantNotificationRequest;
import com.paytm.digital.education.explore.response.dto.notification.MerchantNotificationResponse;
import com.paytm.digital.education.explore.response.dto.notification.NotificationResponseDTO;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MerchantNotificationServiceImpl {

    private static Logger log = LoggerFactory.getLogger(MerchantNotificationServiceImpl.class);

    private MerchantNotificationRepository merchantNotificationRepository;
    private MerchantStreamRepository merchantStreamRepository;

    public MerchantNotificationResponse saveNotification(
            MerchantNotificationRequest merchantNotificationRequest) {

        MerchantNotificationResponse notificationResponse = new MerchantNotificationResponse();

        MerchantNotification merchantNotification = new MerchantNotification();
        BeanUtils.copyProperties(merchantNotificationRequest, merchantNotification);
        merchantNotification.setMerchantUpdatedAt(merchantNotificationRequest.getUpdatedAt());
        Long paytmStreamId = getPaytmStreamId(merchantNotificationRequest.getStream(),
                merchantNotificationRequest.getMerchant());
        if (Objects.isNull(paytmStreamId)) {
            log.info("Stream does not exists. Notification not saved.");
            return updateResponse(notificationResponse,4012,
                    "Stream does not exists. Notification not saved.", null);
        }
        merchantNotification.setPaytmStreamId(paytmStreamId);
        try {
            MerchantNotification dbMerchantNotification =
                    merchantNotificationRepository.save(merchantNotification);
            if (Objects.nonNull(dbMerchantNotification)) {
                NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
                BeanUtils.copyProperties(dbMerchantNotification, notificationResponseDTO);
                notificationResponse = updateResponse(notificationResponse,200,
                        "Notification Saved successfully", notificationResponseDTO);
            }
        } catch (Exception e) {
            log.error("Error saving Notification : ", e);
            notificationResponse = updateResponse(notificationResponse,4012,
                    "Error saving Notification", null);
        }
        return notificationResponse;
    }

    private MerchantNotificationResponse updateResponse(MerchantNotificationResponse
            notificationResponse, int status, String message,
            NotificationResponseDTO notificationResponseDTO) {
        notificationResponse.setStatus(status);
        notificationResponse.setMessage(message);
        notificationResponse.setMerchantNotification(notificationResponseDTO);
        return notificationResponse;
    }

    private Long getPaytmStreamId(String merchantStream, String merchant) {
        MerchantStreamEntity merchantStreamEntity =
                merchantStreamRepository.findByMerchantIdAndStream(merchant, merchantStream);
        if (Objects.nonNull(merchantStreamEntity)) {
            return merchantStreamEntity.getPaytmStreamId();
        }
        return null;
    }

}
