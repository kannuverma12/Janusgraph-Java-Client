package com.paytm.digital.education.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SftpConfig {
    @JsonProperty("username")
    private String username;

    @JsonProperty("key_path")
    private String keyPath;

    @JsonProperty("password")
    private String password;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private Integer port;
}
