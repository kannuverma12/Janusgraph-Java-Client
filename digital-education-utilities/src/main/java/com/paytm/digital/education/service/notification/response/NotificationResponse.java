package com.paytm.digital.education.service.notification.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("error")
    private String error;

    public boolean isSuccess() {
        if (Objects.nonNull(this.code) && code != 202 && StringUtils.isNotBlank(this.error)) {
            return false;
        }
        return true;
    }
}
