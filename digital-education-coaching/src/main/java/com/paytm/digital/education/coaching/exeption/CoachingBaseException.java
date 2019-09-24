package com.paytm.digital.education.coaching.exeption;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoachingBaseException extends Exception {

    public static final long serialVersionUID = 6331963520187396209L;

    protected HttpStatus httpStatus;

    public CoachingBaseException(String message) {
        super(message);
    }

    public CoachingBaseException(Throwable cause, HttpStatus httpStatus, String message) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
