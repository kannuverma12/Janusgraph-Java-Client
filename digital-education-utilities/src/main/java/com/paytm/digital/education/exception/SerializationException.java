package com.paytm.digital.education.exception;

import static com.paytm.digital.education.mapping.ErrorEnum.SOMETHING_BROKE_WHILE_SERIALIZING_DESERIALIZING;

public class SerializationException extends EducationException {
    private static final String INTERNAL_MESSAGE = "Serialization/Deserialization issue occurred";
    private final Throwable cause;

    public SerializationException(Throwable cause) {
        super(SOMETHING_BROKE_WHILE_SERIALIZING_DESERIALIZING, new Object[]{});
        this.cause = cause;
    }
}
