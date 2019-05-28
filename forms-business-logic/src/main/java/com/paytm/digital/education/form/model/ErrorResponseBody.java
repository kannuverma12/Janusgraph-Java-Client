package com.paytm.digital.education.form.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseBody {
    private int statusCode;
    private String error;
}
