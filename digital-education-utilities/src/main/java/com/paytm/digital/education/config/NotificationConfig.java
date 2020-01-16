package com.paytm.digital.education.config;

import static com.paytm.digital.education.constant.CommonConstants.APPLICATION_JSON;
import static com.paytm.digital.education.constant.CommonConstants.CLIENT_ID;
import static com.paytm.digital.education.constant.CommonConstants.CONTENT_TYPE;
import static com.paytm.digital.education.constant.CommonConstants.SECRET_KEY;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class NotificationConfig {

    private static String              clientId;
    private static String              secretKey;
    private static String              smsNotificationUrl;
    private static String              emailNotificationUrl;
    private static Map<String, String> headers = null;

    @Value("${education.notification.client.id}")
    public void setClientId(String notificationClientId) {
        clientId = notificationClientId;
    }

    @Value("${education.notification.secret.key}")
    public void setSecretKey(String notificationSecreKey) {
        secretKey = notificationSecreKey;
    }

    @Value("${education.notification.sms.url}")
    public void setNotificationSmsUrl(String notificationSmsUrl) {
        smsNotificationUrl = notificationSmsUrl;
    }

    @Value("${education.notification.email.url}")
    public void setEmailNotificationUrl(String notificationEmailUrl) {
        emailNotificationUrl = notificationEmailUrl;
    }

    public static String getSmsNotificationUrl() {
        return smsNotificationUrl;
    }

    public static String getEmailNotificationUrl() {
        return emailNotificationUrl;
    }

    public static Map<String, String> getNotificationHeaders() {
        if (Objects.isNull(headers)) {
            headers = new HashMap<>();
            headers.put(CLIENT_ID, clientId);
            headers.put(CONTENT_TYPE, APPLICATION_JSON);
            headers.put(SECRET_KEY, secretKey);
        }
        return headers;
    }
}
