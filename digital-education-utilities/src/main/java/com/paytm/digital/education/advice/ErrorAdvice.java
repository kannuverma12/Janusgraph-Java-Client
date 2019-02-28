package com.paytm.digital.education.advice;

import static com.paytm.digital.education.utility.ArrayUtils.padArray;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.ValidationException;
import com.paytm.digital.education.mapping.ErrorEnum;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
@Slf4j
public class ErrorAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<Set> handleValidationException(ValidationException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getValidationErrors(), HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param ex - Education exception - Top level exception for education
     * @param request - WebRequest request - The request that caused this
     *                  exception
     * @return Spring's Response Entity - containing meaningful message
     *         for the api consumer.
     *
     *          *Note for devs* :- Please extend from EducationException so that your
     *          exceptions can be handled here.
     *
     *          Currently Extending Class - BadAutoSuggestException
     */
    @ExceptionHandler(EducationException.class)
    public final ResponseEntity<ErrorDetails> handleEducationException(
        EducationException ex, WebRequest request) {
        ErrorEnum errorEnum = ex.getErrorEnum();
        ErrorDetails errorDetails =
            new ErrorDetails(
                errorEnum.getInternalCode(),
                String.format(
                    errorEnum.getExternalMessage(),
                    padArray(errorEnum.getNumberOfArgs(), ex.getArgs())),
                ex.getInternalMessage());
        logException(errorDetails.toString(), ex);
        return new ResponseEntity<>(errorDetails, errorEnum.getHttpStatus());
    }

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ErrorDetails> handleRuntimeException(Throwable ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails();
        logException(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logException(String error, Throwable e) {
        log.error(error, e);
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    private static class ErrorDetails {
        @JsonProperty("code")
        private int internalCode;

        @JsonProperty("message")
        private String externalMessage = "Unhandled Exception Happened";

        @JsonIgnore
        private String internalMessage;
    }
}
