package com.paytm.digital.education.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EduCache {
    String[] keys() default {};
    String cache() default "defaultCache";
    boolean shouldCacheNull() default true;
}
