package com.paytm.digital.education.coaching.exeptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoachingClientException extends CoachingBaseException {

    private static final long serialVersionUID = 1L;

    public CoachingClientException(Throwable cause, HttpStatus httpStatus, String message) {
        super(cause, httpStatus, message);
    }

    public CoachingClientException(Throwable cause, HttpStatus httpStatus,
            List<GenericError> errors) {
        super(cause, httpStatus, errors);
    }

    public CoachingClientException(HttpStatus httpStatus, String message) {
        super(message, httpStatus);
    }


    public CoachingClientException(HttpStatus httpStatus, String message, int code) {
        super(message, httpStatus);
        this.code = code;
    }

    public CoachingClientException(HttpStatus httpStatus, List<GenericError> errors) {
        super(httpStatus);
        this.errors = errors;
    }

}
