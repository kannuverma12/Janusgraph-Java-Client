package com.paytm.digital.education.coaching.exeptions;

public class ResourceNotPresentException extends RuntimeException {

    private String message;

    public ResourceNotPresentException(String message) {
        this.message = message;
    }
}
