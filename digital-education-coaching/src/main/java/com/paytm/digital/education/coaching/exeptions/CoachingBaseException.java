package com.paytm.digital.education.coaching.exeptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoachingBaseException extends Exception {

    public static final long serialVersionUID = 6331963520187396209L;

    protected HttpStatus httpStatus;

    protected List<GenericError> errors;

    protected int code;

    public CoachingBaseException(String message) {
        super(message);
    }

    public CoachingBaseException(Throwable cause, HttpStatus httpStatus, String message) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public CoachingBaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public CoachingBaseException(HttpStatus httpStatus, List<GenericError> errors) {
        super();
        this.httpStatus = httpStatus;
        this.errors = errors;
    }

    public CoachingBaseException(Throwable cause, HttpStatus httpStatus,
            List<GenericError> errors) {
        super(cause);
        this.httpStatus = httpStatus;
        this.errors = errors;
    }

    public CoachingBaseException(List<GenericError> errors) {
        super();
        this.errors = errors;
    }

    public CoachingBaseException(int code, String message) {
        super();
        this.code = code;
    }

    public CoachingBaseException(HttpStatus httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }


}
