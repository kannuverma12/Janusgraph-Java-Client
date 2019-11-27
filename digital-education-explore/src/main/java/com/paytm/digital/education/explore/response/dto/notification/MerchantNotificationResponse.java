package com.paytm.digital.education.explore.response.dto.notification;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.MerchantNotification;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantNotificationResponse {

    @JsonProperty("status")
    private Integer status ;

    @JsonProperty("message")
    private String message;

    @JsonProperty("merchant_notification")
    private MerchantNotification merchantNotification;

}
