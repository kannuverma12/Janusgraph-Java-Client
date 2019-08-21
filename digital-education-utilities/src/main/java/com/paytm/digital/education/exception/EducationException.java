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

    public EducationException(ErrorEnum errorEnum, Object[] args) {
        this(errorEnum, null, args);
    }

    public String getMessage() {
        if (internalMessage != null) {
            return internalMessage;
        }

        if (errorEnum != null) {
            return String.format(this.errorEnum.getExternalMessage(), this.args);
        }

        return super.getMessage();
    }
}
