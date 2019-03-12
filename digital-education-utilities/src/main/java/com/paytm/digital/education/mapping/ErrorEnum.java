package com.paytm.digital.education.mapping;

import org.springframework.http.HttpStatus;

public enum ErrorEnum {
    UNEXPECTED_ERROR(5001, "Something unexpected happened", HttpStatus.INTERNAL_SERVER_ERROR, 0),
    BAD_AUTO_SUGGEST_QUERY_ERROR(4001, "Bad Request. Search Term can't be empty.",
            HttpStatus.BAD_REQUEST, 0),
    MIN_LENGTH_ERROR(4002, "Must have minimum length of %s", HttpStatus.BAD_REQUEST, 1),
    MAX_LENGTH_ERROR(4003, "Must have maximum length of %s", HttpStatus.BAD_REQUEST, 1),
    INVALID_RANGE_FILTER_VALUES(4004, "Invalid range filter. Filter %s size should be 2",
            HttpStatus.BAD_REQUEST, 1),
    INVALID_EXAM_ID(4005, "Invalid exam ID. Please provide a valid exam ID.",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_FIELD_GROUP(4006, "No such field_group exists. Please provide a valid field_group",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_INSTITUTE_ID(4007, "Invalid institute ID. Please provide a valid institute ID.",
            HttpStatus.BAD_REQUEST, 0);

    private final int        internalCode;
    private final String     externalMessage;
    private final HttpStatus httpStatus;
    private final int        numberOfArgs;

    ErrorEnum(int internalCode, String externalMessage, HttpStatus httpStatus, int numberOfArgs) {
        this.internalCode = internalCode;
        this.externalMessage = externalMessage;
        this.httpStatus = httpStatus;
        this.numberOfArgs = numberOfArgs;
    }

    public int getInternalCode() {
        return internalCode;
    }

    public String getExternalMessage() {
        return externalMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
