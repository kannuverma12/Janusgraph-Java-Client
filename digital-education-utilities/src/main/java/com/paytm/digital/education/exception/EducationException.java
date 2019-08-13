package com.paytm.digital.education.exception;

import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.Getter;

@Getter
public class EducationException extends RuntimeException {
    private final ErrorEnum errorEnum;
    private final String    internalMessage;
    private final Object[]  args;

    public EducationException(ErrorEnum errorEnum, String internalMessage, Object[] args) {
        this(errorEnum, internalMessage, args, null);
    }

    public EducationException(ErrorEnum errorEnum, String internalMessage, Object[] args,
            Throwable cause) {
        super(cause);
        this.errorEnum = errorEnum;
        this.internalMessage = internalMessage;
        this.args = args;
    }

    public EducationException(ErrorEnum errorEnum, String internalMessage) {
        this(errorEnum, internalMessage, new Object[] {});
    }
}
