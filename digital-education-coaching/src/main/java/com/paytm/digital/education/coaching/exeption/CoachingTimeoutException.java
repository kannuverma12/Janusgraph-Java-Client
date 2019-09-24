package com.paytm.digital.education.coaching.exeption;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoachingTimeoutException extends CoachingBaseException {

    public static final long serialVersionUID = -2274706034754755846L;

    public CoachingTimeoutException(Throwable cause, HttpStatus httpStatus, String message) {
        super(cause, httpStatus, message);
    }
}
