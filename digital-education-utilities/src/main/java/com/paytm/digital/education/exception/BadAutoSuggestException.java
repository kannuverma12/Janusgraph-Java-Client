package com.paytm.digital.education.exception;

import com.paytm.digital.education.mapping.ErrorEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadAutoSuggestException extends EducationException {
    public BadAutoSuggestException(ErrorEnum errorEnum, String internalMessage, Object[] args) {
        super(errorEnum, internalMessage, args);
    }

    public BadAutoSuggestException(ErrorEnum errorEnum, String internalMessage) {
        this(errorEnum, internalMessage, new Object[]{});
    }
}
