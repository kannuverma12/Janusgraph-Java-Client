package com.paytm.digital.education.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private final Set<ValidationError> validationErrors;

    public static ValidationException buildValidationExceptionForObject(
            Set<ConstraintViolation<Object>> constraintViolations) {
        return new ValidationException(constraintViolations);
    }

    public static ValidationException buildValidationExceptionForUnknown(
            Set<ConstraintViolation<?>> constraintViolations) {
        return new ValidationException(constraintViolations);
    }

    private ValidationException(Set violations) {
        Set<ValidationError> localValidationErrors = new HashSet<>();
        for (Object o : violations) {
            ConstraintViolation v = (ConstraintViolation) o;
            localValidationErrors.add(new ValidationError(v));
        }
        this.validationErrors = localValidationErrors;
    }

    public ValidationException(List<FieldError> fieldErrors) {
        this.validationErrors =
                fieldErrors
                        .stream()
                        .map(ValidationError::new)
                        .collect(toSet());
    }
}
