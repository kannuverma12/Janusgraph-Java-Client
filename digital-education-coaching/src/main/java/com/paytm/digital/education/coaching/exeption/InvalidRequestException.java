package com.paytm.digital.education.coaching.exeption;

public class InvalidRequestException extends RuntimeException {

    private String message;

    public InvalidRequestException(String message) {
        this.message = message;

    }

}
