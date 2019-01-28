package com.paytm.digital.education.application.exception;

import com.paytm.digital.education.application.constant.ErrorCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class InvalidRequestException extends GlobalException {

    private static final long serialVersionUID = -4363298165185391491L;

    private String            reason;

    @Builder
    public InvalidRequestException(final Throwable cause, final ErrorCode errorCode,
            final String message, final String reason) {
        super(HttpStatus.BAD_REQUEST, cause, errorCode, message == null ? reason : message);
        this.reason = reason;
    }
}

