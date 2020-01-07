package com.paytm.digital.education.metrics.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Inherited
public @interface MethodCounter {

    String name() default "";
    int count() default 1;
    boolean recordLatency() default false;
}
