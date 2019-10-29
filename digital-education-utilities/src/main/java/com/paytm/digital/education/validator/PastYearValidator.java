package com.paytm.digital.education.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class PastYearValidator implements ConstraintValidator<PastYear, String> {

    public boolean isValid(String value, ConstraintValidatorContext context) {
        Integer year;
        try {
            year = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            year = Integer.MAX_VALUE;
        }
        return year > 0 && LocalDateTime.now().getYear() >= year;
    }
}
