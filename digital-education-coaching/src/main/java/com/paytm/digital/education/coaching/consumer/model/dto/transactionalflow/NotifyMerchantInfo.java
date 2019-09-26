package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotifyMerchantInfo {

    @NotNull
    private Long merchantId;

    private String mobileNumber;
    private String emailId;

    @NotEmpty
    private String accessKey;

    @NotEmpty
    private String secretKey;

    @NotEmpty
    private String endPoint;

    @NotEmpty
    private String host;
}
