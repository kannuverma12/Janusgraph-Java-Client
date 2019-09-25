package com.paytm.digital.education.application.advice;

import static com.paytm.digital.education.explore.constants.ExploreConstants.ERROR_IN_FIELD_VALUE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.USER_UNAUTHORIZED_MESSAGE;
import static com.paytm.digital.education.utility.ArrayUtils.padArray;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.ValidationException;
import com.paytm.digital.education.mapping.ErrorEnum;

import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolationException;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestController
@RestControllerAdvice
public class ErrorAdvice extends ResponseEntityExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(ErrorAdvice.class);

    @ExceptionHandler(MissingRequestHeaderException.class)
    public final ResponseEntity handleMissingHeader(MissingRequestHeaderException ex, WebRequest request) {
        logException(ex.getLocalizedMessage(), ex);
        String headerName = ex.getHeaderName();
        if ("x-user-id".equals(headerName)) {
            return new ResponseEntity<>(
                new ErrorDetails(1, USER_UNAUTHORIZED_MESSAGE, USER_UNAUTHORIZED_MESSAGE),
                HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<Set> handleValidationException(ValidationException ex, WebRequest request) {
        logException(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(ex.getValidationErrors(), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> exceptionHandler(ConstraintViolationException ex) {
        logException(ex.getLocalizedMessage(), ex);
        ValidationException v = ValidationException.buildValidationException(ex.getConstraintViolations());
        return new ResponseEntity<>(v.getValidationErrors(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logException(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(ValidationException.ValidationError.fromFieldErrors(
            ex.getBindingResult().getFieldErrors()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        logException(ex.getLocalizedMessage(), ex);
        String errorMessage = String.format("Incorrect value %s for field %s", ex.getValue(), ex.getName());
        return new ResponseEntity<>(new ErrorDetails(1, errorMessage, errorMessage),
            HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable throwableCause = ex.getCause();
        logException(ex.getLocalizedMessage(), ex);
        if (throwableCause instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) throwableCause;
            Optional<String> fieldName = cause.getPath().stream().map(
                    JsonMappingException.Reference::getFieldName).filter(
                    StringUtils::isNotBlank).findFirst();
            String errorMessage = String.format(ERROR_IN_FIELD_VALUE_TEMPLATE,
                    cause.getValue(), fieldName.orElse(EMPTY));
            return new ResponseEntity<>(new ErrorDetails(1, errorMessage, errorMessage),
                HttpStatus.BAD_REQUEST);
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String name = ex.getParameterName();
        String errorMessage = name + " is missing";
        return new ResponseEntity<>(new ErrorDetails(400, errorMessage, errorMessage),
                HttpStatus.BAD_REQUEST);
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
