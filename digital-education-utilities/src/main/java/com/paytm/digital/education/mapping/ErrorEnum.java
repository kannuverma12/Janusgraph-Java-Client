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
            HttpStatus.BAD_REQUEST, 0),
    RANGE_TYPE_FILTER_VALUES_ERROR(4008, "Range type filter must have length if %s",
            HttpStatus.BAD_REQUEST, 1),
    FILTER_DOESNOT_EXIST(4009, "Applied filter doesnot exist", HttpStatus.BAD_REQUEST, 1),
    ENTITY_NOT_SUBSCRIBED(4010, "Requested entity is not subscribed. Please subscribe first.",
            HttpStatus.BAD_REQUEST,
            0),
    INVALID_COURSE_ID(4011, "Invalid course ID. Please provide a valid course ID.",
            HttpStatus.BAD_REQUEST, 0),
    ENTITY_ID_IS_MANDATORY(4012, "entity id is mandatory for course search", HttpStatus.BAD_REQUEST,
            0),
    INVALID_ENTITY_ID_LIST_SIZE(4013, "entity id must contain one and only one value",
            HttpStatus.BAD_REQUEST, 0),
    QUERY_TERM_MUST_BE_EMPTY(4014, "query term is not supported in course search",
            HttpStatus.BAD_REQUEST, 0),
    NO_LIST_EXISTS(4015, "No list exists for this combination.",
            HttpStatus.NOT_FOUND, 0),
    NO_CUTOFF_EXISTS(4016, "Cutoff does not exists for this combination.",
            HttpStatus.NOT_FOUND, 0),
    NO_EXAM_LIST_EXISTS(4017, "No exam lists exists for this institute",
            HttpStatus.NOT_FOUND, 0),
    GENDER_PARAMETER_MISSING(4018, "Gender parameter is missing in request.",
            HttpStatus.BAD_REQUEST, 0),
    CASTE_CATEGORY_PARAMETER_MISSING(4019, "Caste category parameter is missing in request.",
            HttpStatus.BAD_REQUEST, 0),
    ORDER_ID_OR_START_DATE(9000, "Either Order Id or start date must be present.",
            HttpStatus.BAD_REQUEST, 10),
    EITHER_OF_ORDER_ID_OR_START_DATE(9001, "Order Id or start date both are not allowed together.",
            HttpStatus.BAD_REQUEST, 10),
    USER_IS_NOT_MERCHANT(9002, "User is not Merchant",
            HttpStatus.UNAUTHORIZED, 0),
    MISSING_FORM_DATA_PARAMS(9003,
            "Bad request. Some mandatory params are missing", HttpStatus.BAD_REQUEST, 0),
    UNAUTHORIZED_REQUEST(9004, "Unauthorized request.", HttpStatus.UNAUTHORIZED, 0),
    PAYMENT_CONFIGURATION_NOT_FOUND(9005,
            "Payment configuration doesn't exists for, merchantId : %s and merchantSku : %s",
            HttpStatus.INTERNAL_SERVER_ERROR, 2);

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
