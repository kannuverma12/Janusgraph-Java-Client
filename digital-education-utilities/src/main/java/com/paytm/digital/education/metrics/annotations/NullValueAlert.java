package com.paytm.digital.education.metrics.annotations;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Inherited
public @interface NullValueAlert {
    String name() default "";
    String mandatoryFields() ;
}
