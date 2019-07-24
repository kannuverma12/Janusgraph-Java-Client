package com.paytm.digital.education.deal.response.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDto {

    private String  message;
    private Integer code;

    public ResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
