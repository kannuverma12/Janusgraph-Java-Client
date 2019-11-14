package com.paytm.digital.education.explore.response.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class DataImportResponse {

    @JsonProperty("status-code")
    private int statusCode;

    @JsonProperty("http-status")
    private HttpStatus httpStatus;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private String error;

}
