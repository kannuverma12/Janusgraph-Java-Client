package com.paytm.digital.education.exception;

import com.paytm.digital.education.mapping.ErrorEnum;

public class UnableToAccessBeanPropertyException extends EducationException {
    public UnableToAccessBeanPropertyException(Object bean, String message) {
        super(ErrorEnum.CACHE_BEAN_KEY_INACCESSIBLE, new Object[]{bean, message});
    }
}
