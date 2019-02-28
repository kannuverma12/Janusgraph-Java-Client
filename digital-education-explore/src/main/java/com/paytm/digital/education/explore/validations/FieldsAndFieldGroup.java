package com.paytm.digital.education.explore.validations;

import com.paytm.digital.education.explore.validators.FieldsAndFieldGroupValidator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

/**
 *  <p>
 *  This annotation is used for validating education's
 *  fields and field_group policy and is validated by
 *  FieldsAndFieldGroupValidator class. It is already applied
 *  on FieldsAndFieldGroupRequest Pojo class. If you expect
 *  fields and field_group request params in your API. You can
 *  simple use FieldsAndFieldGroupRequest.
 *  </p>
 *
 *  <p>
 *  Explanation of meta annotations
 *    1. Retention - Retention Policy for this annotation. We
 *       need 'RUNTIME' so that this annotation can be used by JVM
 *
 *    2. Target - To decide where this annotation can be used.
 *       We need 'TYPE' so that this annotation can be used on class.
 *       Targets can be method args, fields, etc
 *
 *    3. Constraint - To specify the validator implementation
 *  </p>
 */

@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = FieldsAndFieldGroupValidator.class)
public @interface FieldsAndFieldGroup {
    String message() default "Must have either field or field group";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
