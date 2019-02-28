package com.paytm.digital.education.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;


@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private final Set<ValidationError> validationErrors;

    private ValidationException(Set<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public static <T> ValidationException buildValidationException(Set<T> violations) {
        Set<ValidationError> validationErrors = ValidationError.fromViolations(violations);
        return new ValidationException(validationErrors);
    }

    @Data
    public static class ValidationError {

        private String propertyPath;
        private String errorMessage;

        private ValidationError() {}

        public static Set<ValidationError> fromViolations(Set violations) {
            Set<ValidationError> errors = new HashSet<ValidationError>();

            for (Object o : violations) {
                ConstraintViolation v = (ConstraintViolation) o;

                ValidationError error = new ValidationError();
                error.setErrorMessage(v.getMessage());
                error.setPropertyPath(v.getPropertyPath().toString());
                errors.add(error);
            }

            return errors;
        }

        @Override
        public String toString() {
            return "ValidationError{"
                + ", propertyPath='" + propertyPath + '\''
                + ", errorMessage='" + errorMessage + '\''
                + '}';
        }
    }
}
