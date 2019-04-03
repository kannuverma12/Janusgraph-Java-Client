package com.paytm.digital.education.exception;

import com.paytm.digital.education.mapping.ErrorEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends EducationException {
    public NotFoundException(ErrorEnum errorEnum, String internalMessage, Object[] args) {
        super(errorEnum, internalMessage, args);
    }

    public NotFoundException(ErrorEnum errorEnum, String internalMessage) {
        this(errorEnum, internalMessage, new Object[] {});
    }
}
