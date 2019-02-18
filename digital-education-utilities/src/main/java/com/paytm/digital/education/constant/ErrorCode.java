package com.paytm.digital.education.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {

    METHOD_ARGUMENTS("001", "Invalid request - Method Arguments"),
    MALFORMED_JSON_REQUEST("002", "Malformed Json Request"),
    UNAUTHORIZED_REQUEST("003", "Unauthorized Request"),
    UNSUPPORTED_MEDIATYPE("004", "Unsupported Media Type"),

    DP_INVALID_CALL("DP_001", "Dependency - Invalid API/DB Call"),
    DP_TIMEOUT("DP_002", "Dependency - API/DB Call Timeout"),
    DP_INVALID_RESPONSE("DP_003", "Dependency - Invalid API/DB response"),
    DP_INVALID_REQUEST("DP_004", "Dependency - Invalid API/DB request"),
    DP_USER_UNAUTHORIZED("DP_005", "Dependency - Unauthorized API/DB call"),
    DP_RESOURCE_NOT_FOUND("DP_006", "Dependency - API/DB Resource not found"),
    DP_MISSING_REQUIRED_FIELD("DP_007", "Dependency - API/DB Missing required field"),
    DP_RESOURCE_ACCESS_EXCEPTION("DP_008", "Dependency - Resource Access Exception"),

    SAE_PERSONA_TEMPLATE_NOT_FOUND("SAE_004", "Persona Template not found"),
    
    ORS_ORDER_PROCESSING_FAILED("ORS_001", "Order Processing failed");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
