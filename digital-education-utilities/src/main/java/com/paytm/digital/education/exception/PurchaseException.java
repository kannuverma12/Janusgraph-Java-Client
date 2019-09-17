package com.paytm.digital.education.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseException extends Exception {

    private HttpStatus httpStatus;
    private String     message;
    private Exception  cause;
    private Boolean    acknowledged;
}
