package com.paytm.digital.education.mapping;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpStatus;

public enum ErrorEnum {
    UNEXPECTED_ERROR(5001, "Something unexpected happened", HttpStatus.INTERNAL_SERVER_ERROR, 0),
    BAD_AUTO_SUGGEST_QUERY_ERROR(4001, "Bad Request. Search Term can't be empty.",
            BAD_REQUEST, 0),
    MIN_LENGTH_ERROR(4002, "Must have minimum length of %s", BAD_REQUEST, 1),
    MAX_LENGTH_ERROR(4003, "Must have maximum length of %s", BAD_REQUEST, 1),
    INVALID_RANGE_FILTER_VALUES(4004, "Invalid range filter. Filter %s size should be 2",
            BAD_REQUEST, 1),
    INVALID_EXAM_ID(4005, "Invalid exam ID. Please provide a valid exam ID.",
            BAD_REQUEST, 0),
    INVALID_FIELD_GROUP(4006, "No such field_group exists. Please provide a valid field_group",
            BAD_REQUEST, 0),
    INVALID_INSTITUTE_ID(4007, "Invalid institute ID. Please provide a valid institute ID.",
            BAD_REQUEST, 0),
    RANGE_TYPE_FILTER_VALUES_ERROR(4008, "Range type filter must have length if %s",
            BAD_REQUEST, 1),
    FILTER_DOESNOT_EXIST(4009, "Applied filter doesnot exist", BAD_REQUEST, 1),
    ENTITY_NOT_SUBSCRIBED(4010, "Requested entity is not subscribed. Please subscribe first.",
            BAD_REQUEST,
            0),
    INVALID_COURSE_ID(4011, "Invalid course ID. Please provide a valid course ID.",
            BAD_REQUEST, 0),
    ENTITY_ID_AND_ENTITY_NAME_IS_MANDATORY(4012,
            "entity id and entity name both are mandatory for course search",
            BAD_REQUEST,
            0),
    INVALID_ENTITY_ID_LIST_SIZE(4013, "entity id must contain one and only one value",
            BAD_REQUEST, 0),
    QUERY_TERM_MUST_BE_EMPTY(4014, "query term is not supported in course search",
            BAD_REQUEST, 0),
    NO_LIST_EXISTS(4015, "No list exists for this combination.",
            HttpStatus.NOT_FOUND, 0),
    NO_CUTOFF_EXISTS(4016, "Cutoff does not exists for this combination.",
            HttpStatus.NOT_FOUND, 0),
    NO_EXAM_LIST_EXISTS(4017, "No exam lists exists for this institute",
            HttpStatus.NOT_FOUND, 0),
    GENDER_PARAMETER_MISSING(4018, "Gender parameter is missing in request.",
            BAD_REQUEST, 0),
    CASTE_CATEGORY_PARAMETER_MISSING(4019, "Caste category parameter is missing in request.",
            BAD_REQUEST, 0),
    ORDER_ID_OR_START_DATE(9000, "Either Order Id or start date must be present.",
            BAD_REQUEST, 10),
    EITHER_OF_ORDER_ID_OR_START_DATE(9001, "Order Id or start date both are not allowed together.",
            BAD_REQUEST, 10),
    USER_IS_NOT_MERCHANT(9002, "User is not Merchant",
            HttpStatus.UNAUTHORIZED, 0),
    MISSING_FORM_DATA_PARAMS(9003,
            "Bad request. Some mandatory params are missing", BAD_REQUEST, 0),
    UNAUTHORIZED_REQUEST(9004, "Unauthorized request.", HttpStatus.UNAUTHORIZED, 0),
    PAYMENT_CONFIGURATION_NOT_FOUND(9005,
            "Payment configuration doesn't exists for, merchantId : %s and merchantSku : %s",
            HttpStatus.INTERNAL_SERVER_ERROR, 2),
    ENTITY_ID_NOT_PRESENT(4020, "%s is missing in the request.",
            BAD_REQUEST, 1),
    INVALID_INSTITUTE_NAME(4021, "Provided name doesnot match with actual institute name",
            BAD_REQUEST, 0),
    INVALID_EXAM_NAME(4022, "Provided name doesnot match with actual exam name",
            BAD_REQUEST, 0),
    INVALID_COURSE_NAME(4023, "Provided name doesnot match with actual course name",
            BAD_REQUEST, 0),
    INSTITUTE_NAME_OR_ID_MISSING(4024, "Institute name and id both should be present",
            BAD_REQUEST, 0),
    VALID_INSTITUTE_ID_FOR_COURSE_LEAD(4025, "Provide valid institute id for course lead",
            BAD_REQUEST, 0),
    COURSE_IS_NOT_ACCEPTING_APPLICATION(4026, "Provided course is not accepting application",
            BAD_REQUEST, 0),
    ENTITY_NOT_SUPPORTED_FOR_LEAD(4027, "Provided entity is not supported to send lead",
            BAD_REQUEST, 0),
    HTTP_REQUEST_FAILED(4028, "Http request failed %s",
            BAD_REQUEST, 1),
    STREAM_IS_MANDATORY_FOR_COURSE_LEAD(4029, "Stream is mandatory",
            BAD_REQUEST, 0),
    ACTION_NOT_SUPPORTED(4030, "Requested action is not supported for given entity",
            BAD_REQUEST, 0),
    INSTITUTE_ID_AND_ENTITY_IS_MANDATORY_FOR_UNFOLLOW(4031,
            "Institute id and entity type is mandatory", BAD_REQUEST, 0),
    ENTITY_TYPE_IS_MANDATORY_FOR_UNFOLLOW(4032, "Entity type is mandatory", BAD_REQUEST,
            0),
    NO_SUCH_ENTITY_EXISTS(4033, "No such %s exists. Either %s ID %s is inactive or doesn't exists.",
            BAD_REQUEST, 3),
    ORDER_ID_AND_EOD_BOTH_CANNOT_BE_NULL(4034, "Both order id and eod cannot be null",
            BAD_REQUEST, 0),
    INVALID_EOD(4035, "provided eod is invalid", BAD_REQUEST, 0),
    BULK_REQUEST_LIMIT_EXCEEDED(4036, "Bulk request size should be less than 200.",
            BAD_REQUEST, 0),
    ENTITY_ID_MANDATORY_FOR_SUBSCRIPTION(4037, "Entity id is mandatory for subscription requests.",
            BAD_REQUEST, 0),
    INVALID_CUSTOMER_ID(4038, "Invalid customer ID. Please provide a valid customer ID.",
            BAD_REQUEST, 0),
    USER_INFO_MISMATCH(4039, "Provided user details doesn't matches to info stored at our end",
            BAD_REQUEST, 0),
    INVALID_ENTITY_FOR_DATA_IMPORT(4040, "Provided entity is invalid.", BAD_REQUEST, 0),
    USER_DATA_DOESNOT_EXISTS(4041, "User data does not exists.", BAD_REQUEST, 0),
    INVALID_FILE_VERSION(4042, "File is not present for provided version number",
            BAD_REQUEST, 0),
    SFTP_CONNECTION_FAILED(4043, "Unable to connect to sftp.", BAD_REQUEST, 0),
    CORRUPTED_FILE(4044, "The file you are trying to read is corrupted", BAD_REQUEST, 0),
    DATA_NOT_PRESENT(4045, "Data is missing!!!", BAD_REQUEST, 0),
    FUNCTIONALITY_NOT_SUPPORTED_FOR_ENTITY(4046,
            "This functionality is not supported for provided entity", BAD_REQUEST, 0),
    INVALID_SCHOOL_ID(4047, "Invalid school ID. Please provide a valid school ID.",
            BAD_REQUEST, 0),
    NO_ENTITY_FOUND(4048, "No %s is found for the given %s - %s", BAD_REQUEST, 3),
    PID_MISSING(4049, "PID doesnot exists for entity %s", BAD_REQUEST, 1),
    PREDICTOR_ID_MISSING(4050, "COLLEGE PREDICTOR ID doesnot exists for entity %s",
            BAD_REQUEST, 1),
    INVALID_SCHOOL_NAME(4051, "Provided name doesnot match with actual school name",
            BAD_REQUEST, 0),
    INVALID_UPLOAD_REQUEST(4052, "Please choose at least one file to upload",
            BAD_REQUEST, 0),
    INVALID_ENTITY_ID(4053, "Invalid %1$s ID. Please provide a valid %1$s ID.",
            BAD_REQUEST, 1),
    ENTITY_MISSING(4054, "entity field is required.", BAD_REQUEST, 0),
    LAT_OR_LON_MISSING(4055, "Latitude and longitude are mandatory in location.",
            BAD_REQUEST, 0),
    LAT_INVALID(4056, "Please provide valid latitude in request.",
            BAD_REQUEST, 0),
    LON_INVALID(4057, "Please provide valid longitude in request.",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_SORT_FIELD(4058, "Invalid requested sort field(s).", HttpStatus.BAD_REQUEST, 0),
    INVALID_COURSE_ID_AND_URL_DISPLAY_KEY(4059,
            "Invalid course ID/ url display key. Please provide a valid course ID and url display key.",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_STREAM_ID(4060, "Invalid stream ID. Please provide a valid stream ID.",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_STREAM_NAME(4061, "Invalid stream name. Please provide a valid stream name.",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_CART_ITEMS(4062, "Invalid Cart Items. Please provide valid Cart Items",
            HttpStatus.BAD_REQUEST, 0),
    ENTITY_LIST_EMPTY(4063, "Atleast one entity is required.", HttpStatus.BAD_REQUEST, 0),
    INVALID_MERCHANT_ID(4064, "Item with given merchant Id does not exist",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_MERCHANT_DATA(4065, "Invalid merchant info. Please provide valid merchant info.",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_MERCHANT_PRODUCTS(4066,
            "Empty product list received. Please try with at least one product",
            HttpStatus.BAD_REQUEST, 0),
    INVALID_PAGE_NAME(4067, "Provided page name is invalid.", HttpStatus.BAD_REQUEST, 0),
    ENTITY_FIELD_MISSING_IN_DATABASE(4059, "Field - %s of entity - %s missing in database.",
            HttpStatus.SERVICE_UNAVAILABLE, 2),
    ERROR_IN_IMPORT(4060, "%s ", BAD_REQUEST, 1),
    INVALID_DATA_INGESTION_ENTITY(4061, "Requested entity : %s is not supported for data ingestion",
            BAD_REQUEST, 1),
    BLANK_SHEET_ID(4062, "Sheet id not provided. Please provide a valid sheet id",
            BAD_REQUEST, 0),
    SHEET_INFO_NOT_DEFINED(4063, "Sheet info for %s form is not defined", BAD_REQUEST, 1),
    NO_SUCH_PAYTM_STREAM(4064, "No such paytm stream : %s exists in the system", BAD_REQUEST, 1),
    NO_SUCH_MERCHANT_STREAM(4065, "No such stream : %s for merchant: %s exists in the system", BAD_REQUEST,
            2),
    DUPLICATE_STREAM_DATA(4066, "Duplicate data for exam stream mapping for exam_id : %s",
            BAD_REQUEST, 1),
    INVALID_STREAM_MERCHANT_STREAM_MAPPING(4067,
            "Invalid Paytm stream to merchant stream mapping. Paytm Stream : %s, merchantStream : %s",
            BAD_REQUEST, 2),
    BLANK_PAYTM_STREAM_NAME(4068, "Stream name is mandatory.", BAD_REQUEST, 0),
    INVALID_MERCHANT_STREAM_DATA(4069,
            "Mandatory params missing. Mandatory params are : Merchant Stream and paytm_stream_id",
            BAD_REQUEST, 0),
    INVALID_PAYTM_STREAM(4070,
            "Requested paytm stream id : %s doesn't exist in our system. Please provide a valid paytm stream id.",
            BAD_REQUEST, 1),
    PAYTM_STREAM_DISABLED(4071, "Requested paytm stream id : %s is disabled.", BAD_REQUEST, 1),
    GEO_DISTANCE_INVALID(4072,
            "Invalid geodistance. Please provide geodistance less than %s kms in request.",
            BAD_REQUEST, 1),
    ENTITY_NOT_SUPPORTED(4073, "Requested entity not supported for this operation.", BAD_REQUEST,
            0);

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
