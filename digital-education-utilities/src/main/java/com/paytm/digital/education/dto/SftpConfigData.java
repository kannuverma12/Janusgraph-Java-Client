package com.paytm.digital.education.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SftpConfigData {
    @JsonProperty("key_path")
    private String keyPath;

    @JsonProperty("username")
    private String username;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("password")
    private String password;
}
