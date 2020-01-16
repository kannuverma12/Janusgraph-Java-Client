package com.paytm.digital.education.service.notification;

import static com.paytm.digital.education.config.NotificationConfig.getEmailNotificationUrl;
import static com.paytm.digital.education.config.NotificationConfig.getNotificationHeaders;
import static com.paytm.digital.education.constant.CommonConstants.EMAIL_ID;
import static com.paytm.digital.education.constant.CommonConstants.NO_REPLY_EMAIL;

import com.paytm.digital.education.service.http.BaseRestApiService;
import com.paytm.digital.education.service.notification.request.NotificationReceiver;
import com.paytm.digital.education.service.notification.request.NotificationRequest;
import com.paytm.digital.education.service.notification.request.NotificationSender;
import com.paytm.digital.education.service.notification.response.NotificationResponse;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class EmailNotificationService {

    @Autowired private BaseRestApiService restApiService;

    public NotificationResponse notify(String templateName, String emailId,
            Map<String, Object> params) {
        if (StringUtils.isBlank(templateName)) {
            log.error("Email template can't be null. Please provide a valid email template");
            return null;
        }
        NotificationRequest notificationRequest = new NotificationRequest();
        //set template name
        notificationRequest.setTemplateName(templateName);

        //set email sender
        NotificationSender sender = new NotificationSender();
        sender.setEmail(NO_REPLY_EMAIL);
        notificationRequest.setSender(sender);

        //set receiver detail
        NotificationReceiver receiver = new NotificationReceiver();
        receiver.setNotificationReceiverType(EMAIL_ID);
        receiver.setNotificationReceiverIdentifier(Arrays.asList(emailId));
        notificationRequest.setNotificationReceiver(receiver);

        //set dynamic params as per template
        notificationRequest.setDynamicParams(params);
        try {
            NotificationResponse response =
                    restApiService.post(getEmailNotificationUrl(), NotificationResponse.class,
                            JsonUtils.toJson(notificationRequest), getNotificationHeaders());
            log.info("Email sent to email : {}, response message : {}", emailId,
                    response.getMessage());
            return response;
        } catch (Exception ex) {
            log.error("Error caught while sending email to emailId : {}, exception : {}", emailId,
                    ex);
        }
        return null;
    }
}
