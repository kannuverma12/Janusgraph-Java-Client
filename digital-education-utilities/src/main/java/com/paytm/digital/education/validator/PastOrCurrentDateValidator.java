package com.paytm.digital.education.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Optional;

public class PastOrCurrentDateValidator
        implements ConstraintValidator<PastOrCurrentDate, LocalDate> {

    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return Optional.ofNullable(value).map(date -> date.isBefore(LocalDate.now())).orElse(false);
    }
}
