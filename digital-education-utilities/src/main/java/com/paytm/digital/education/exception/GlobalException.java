package com.paytm.digital.education.exception;

import com.paytm.digital.education.constant.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = -4458310060546961768L;

    protected HttpStatus httpStatus;
    protected Throwable  cause;
    protected ErrorCode  errorCode;
    protected String     message;

    public GlobalException(HttpStatus httpStatus, Throwable cause, ErrorCode errorCode, String message) {
        super();
        this.httpStatus = httpStatus;
        this.cause = cause;
        this.errorCode = errorCode;
        this.message = message + " " + errorCode.getDescription();
    }

    public GlobalException(HttpStatus httpStatus, String message, Throwable cause) {
        super();
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public GlobalException(HttpStatus httpStatus, String message) {
        super();
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
