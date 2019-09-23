package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyUserInfo;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantNotifyRequest {

    @NotNull
    private List<MerchantNotifyCartItem> cartItems;

    @NotNull
    private Map<Long, NotifyMerchantInfo> merchantData;

    @Valid
    private NotifyUserInfo userData;
}
