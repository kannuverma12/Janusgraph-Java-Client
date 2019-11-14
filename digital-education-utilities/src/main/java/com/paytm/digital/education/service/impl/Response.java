package com.paytm.digital.education.service.impl;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public class Response<X, Y> {
    private X oldValue;
    private Y newValue;

    @Accessors(fluent = true)
    private boolean isOldValue;

    public Response(X oldValue, Y newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.isOldValue = newValue == null;
    }
}
