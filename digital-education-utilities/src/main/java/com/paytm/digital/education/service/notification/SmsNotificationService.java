package com.paytm.digital.education.service.notification;

import static com.paytm.digital.education.config.NotificationConfig.getNotificationHeaders;
import static com.paytm.digital.education.config.NotificationConfig.getSmsNotificationUrl;
import static com.paytm.digital.education.constant.CommonConstants.CUSTOMER_ID;

import com.paytm.digital.education.service.http.BaseRestApiService;
import com.paytm.digital.education.service.notification.request.NotificationReceiver;
import com.paytm.digital.education.service.notification.request.NotificationRequest;
import com.paytm.digital.education.service.notification.response.NotificationResponse;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class SmsNotificationService {

    @Autowired private BaseRestApiService restApiService;

    public NotificationResponse notify(String templateName, String customerId,
            Map<String, Object> params) {
        NotificationRequest notificationRequest = new NotificationRequest();
        //set template name
        notificationRequest.setTemplateName(templateName);

        //set receiver detail
        NotificationReceiver receiver = new NotificationReceiver();
        receiver.setNotificationReceiverType(CUSTOMER_ID);
        receiver.setNotificationReceiverIdentifier(Arrays.asList(customerId));
        notificationRequest.setNotificationReceiver(receiver);

        //set dynamic params as per template
        notificationRequest.setDynamicParams(params);
        try {
            NotificationResponse response =
                    restApiService.post(getSmsNotificationUrl(), NotificationResponse.class,
                            JsonUtils.toJson(notificationRequest), getNotificationHeaders());
            log.info(
                    "SMS sent to customerId : {}, response message : {}", customerId,
                    response.getMessage());
            return response;
        } catch (Exception ex) {
            log.error("Exception caught while sending sms to customerId : {}, exception: {}",
                    customerId, ex);
        }
        return null;
    }

}
