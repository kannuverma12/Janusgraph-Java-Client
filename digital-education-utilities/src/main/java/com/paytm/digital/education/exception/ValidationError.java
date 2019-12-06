package com.paytm.digital.education.exception;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;

@Data
@ToString
public class ValidationError {

    private String propertyPath;
    private String errorMessage;

    public ValidationError(ConstraintViolation constraintViolation) {
        this.propertyPath = constraintViolation.getPropertyPath().toString();
        this.errorMessage = constraintViolation.getMessage();
    }

    public ValidationError(FieldError fieldError) {
        this.propertyPath = fieldError.getField();
        this.errorMessage = fieldError.getDefaultMessage();
    }
}
