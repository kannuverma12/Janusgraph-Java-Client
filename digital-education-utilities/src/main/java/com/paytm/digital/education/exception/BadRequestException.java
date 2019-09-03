package com.paytm.digital.education.exception;

import com.paytm.digital.education.mapping.ErrorEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends EducationException {
    public BadRequestException(ErrorEnum errorEnum, String internalMessage, Object[] args) {
        super(errorEnum, internalMessage, args);
    }

    public BadRequestException(ErrorEnum errorEnum, String internalMessage) {
        this(errorEnum, internalMessage, new Object[] {});
    }

    public BadRequestException(ErrorEnum errorEnum, Object[] args) {
        this(errorEnum, null, args);
    }
}
