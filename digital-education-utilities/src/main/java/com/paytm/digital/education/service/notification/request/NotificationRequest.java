package com.paytm.digital.education.service.notification.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationRequest {

    @JsonProperty("debug")
    private boolean debug;

    @JsonProperty("templateName")
    private String templateName;

    @JsonProperty("notificationReceiver")
    private NotificationReceiver notificationReceiver;

    @JsonProperty("sender")
    private NotificationSender sender;

    @JsonProperty("replyTo")
    private String replyTo;

    @JsonProperty("dynamicParams")
    private Map<String, Object> dynamicParams;
}
