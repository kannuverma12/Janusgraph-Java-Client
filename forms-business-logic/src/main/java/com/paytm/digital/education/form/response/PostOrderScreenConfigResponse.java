package com.paytm.digital.education.form.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PostOrderScreenConfigResponse {

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("screen_config")
    private Map<String, Object> screenConfig;
}
