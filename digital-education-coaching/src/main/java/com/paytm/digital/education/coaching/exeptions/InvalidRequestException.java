package com.paytm.digital.education.coaching.exeptions;

public class InvalidRequestException extends RuntimeException {

    private String message;

    public InvalidRequestException(String message) {
        this.message = message;

    }

}
