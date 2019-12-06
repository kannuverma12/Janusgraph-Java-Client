package com.paytm.digital.education.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CachedMethodInvocationException extends Exception {
    private final Throwable cause;
}
