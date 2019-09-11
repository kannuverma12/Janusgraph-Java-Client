package com.paytm.digital.education.coaching.exeptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericError<T> {
    private int    code;
    private String message;
    private T      meta;

    public GenericError(int code, String message, T meta) {
        super();
        this.code = code;
        this.message = message;
        this.meta = meta;
    }

    public GenericError(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

}
