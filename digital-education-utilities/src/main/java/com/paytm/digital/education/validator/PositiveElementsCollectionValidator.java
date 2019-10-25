package com.paytm.digital.education.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PositiveElementsCollectionValidator implements
        ConstraintValidator<PositiveElementsCollection, List<Long>> {

    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {
        return !value.stream().anyMatch(data -> data.intValue() < 0);
    }
}
