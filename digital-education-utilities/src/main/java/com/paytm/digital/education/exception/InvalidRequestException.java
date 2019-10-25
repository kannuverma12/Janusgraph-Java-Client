package com.paytm.digital.education.exception;

import com.paytm.digital.education.constant.ErrorCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends GlobalException {

    private static final long serialVersionUID = -4363298165185391491L;

    private String            reason;

    @Builder
    public InvalidRequestException(final Throwable cause, final ErrorCode errorCode,
            final String message, final String reason) {
        super(HttpStatus.BAD_REQUEST, cause, errorCode, message == null ? reason : message);
        this.reason = reason;
    }

    public InvalidRequestException(final String message, final Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }

    public InvalidRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}

