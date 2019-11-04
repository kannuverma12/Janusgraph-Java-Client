package com.paytm.digital.education.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = PastYearValidator.class)
public @interface PastYear {
    String message() default "Year should be from a past time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
