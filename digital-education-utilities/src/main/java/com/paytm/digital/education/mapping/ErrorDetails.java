package com.paytm.digital.education.mapping;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorDetails {
    private final int internalCode;
    private final String responseMessage;
    private final HttpStatus httpStatus;
}
