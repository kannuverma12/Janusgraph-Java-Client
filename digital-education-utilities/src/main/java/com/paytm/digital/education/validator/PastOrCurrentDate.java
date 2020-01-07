package com.paytm.digital.education.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = PastOrCurrentDateValidator.class)
public @interface PastOrCurrentDate {
    String message() default "Date should be from current or past time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
