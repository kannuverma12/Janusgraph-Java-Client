package com.paytm.digital.education.coaching.exeption;

public class ResourceNotPresentException extends RuntimeException {

    private String message;

    public ResourceNotPresentException(String message) {
        this.message = message;
    }
}
