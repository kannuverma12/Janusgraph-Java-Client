package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyUserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantNotifyRequest {

    @NotEmpty
    @Valid
    @ApiModelProperty(required = true)
    private List<MerchantNotifyCartItem> cartItems;

    @NotEmpty
    @Valid
    @ApiModelProperty(required = true)
    private Map<Long, NotifyMerchantInfo> merchantData;

    @NotNull
    @Valid
    @ApiModelProperty(required = true)
    private NotifyUserInfo userData;
    
    @NotEmpty
    @ApiModelProperty(required = true)
    private String orderCreatedAt;
}
