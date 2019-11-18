package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotifyMerchantInfo {

    @NotNull
    @ApiModelProperty(required = true)
    private Long merchantId;

    private String mobileNumber;
    private String emailId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String accessKey;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String secretKey;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String notifyEndpoint;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String host;
}
