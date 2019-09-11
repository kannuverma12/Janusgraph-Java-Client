package com.paytm.digital.education.coaching.exeptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoachingServerException extends CoachingBaseException {

    private static final long serialVersionUID = 1L;

    public CoachingServerException(Throwable cause, HttpStatus httpStatus, String message) {
        super(cause, httpStatus, message);
    }

    public CoachingServerException(Throwable cause, HttpStatus httpStatus,
            List<GenericError> genericErrors) {
        super(cause, httpStatus, genericErrors);
    }

    public CoachingServerException(HttpStatus httpStatus, List<GenericError> errors) {
        super(httpStatus);
        this.errors = errors;
    }
}
