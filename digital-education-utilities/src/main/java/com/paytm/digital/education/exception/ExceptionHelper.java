package com.paytm.digital.education.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paytm.digital.education.constant.ErrorCode;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@UtilityClass
public class ExceptionHelper {

    private static final String HTTP_STATUS = "HttpStatus";
    private static final String ERROR_MESSAGE = "ErrorMessage";
    private static final String ERROR_CODE = "ErrorCode";
    private static final String REASON = "Reason";
    private static final String DEPENDENCY_NAME = "DependencyName";

    @Autowired
    private ObjectMapper objectMapper;

    public DependencyException buildDependencyException(String dependencyName,
                                                        ErrorCode errorCode, String message) {
        return DependencyException.builder().dependencyName(dependencyName)
                .message(message == null ? errorCode.getDescription() : message)
                .errorCode(errorCode)
                .build();
    }

    public DependencyException buildDependencyException(String dependencyName,
                                                        HttpStatus httpStatus, String message, Throwable error) {
        ErrorCode errorCode = ErrorCode.DP_INVALID_CALL;
        switch (httpStatus) {
            case FORBIDDEN:
                errorCode = ErrorCode.DP_USER_UNAUTHORIZED;
                break;
            case GATEWAY_TIMEOUT:
                break;
            case PRECONDITION_FAILED:
                errorCode = ErrorCode.DP_INVALID_REQUEST;
                break;
            case PRECONDITION_REQUIRED:
                errorCode = ErrorCode.DP_MISSING_REQUIRED_FIELD;
                break;
            case PROXY_AUTHENTICATION_REQUIRED:
                errorCode = ErrorCode.DP_USER_UNAUTHORIZED;
                break;
            case REQUEST_TIMEOUT:
                errorCode = ErrorCode.DP_TIMEOUT;
                break;
            case UNAUTHORIZED:
                errorCode = ErrorCode.DP_USER_UNAUTHORIZED;
                break;
            case EXPECTATION_FAILED:
                errorCode = ErrorCode.DP_INVALID_REQUEST;
                break;
            default:
                errorCode = ErrorCode.DP_INVALID_CALL;
                break;
        }

        return DependencyException.builder().dependencyName(dependencyName)
                .message(message + " " + errorCode.getDescription())
                .errorCode(errorCode)
                .cause(error).build();
    }

    @SneakyThrows
    public String buildDependencyExceptionResponse(DependencyException ex) {
        DependencyException dex =
                buildDependencyException(ex.getDependencyName(), ex.getHttpStatus(),
                        ex.getMessage(), ex);
        ObjectNode objectNode = buildBaseExceptionResponse(dex);
        objectNode.put(DEPENDENCY_NAME, ex.getDependencyName());
        return objectNode.toString();
    }

    @SneakyThrows
    public String buildInvalidRequestExceptionResponse(InvalidRequestException ex) {
        ObjectNode objectNode = buildBaseExceptionResponse(ex);
        objectNode.put(REASON, ex.getReason());
        return objectNode.toString();
    }

    @SneakyThrows
    public ObjectNode buildBaseExceptionResponse(GlobalException ex) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(ERROR_CODE, ex.getErrorCode().getCode());
        objectNode.put(ERROR_MESSAGE, ex.getMessage());
        objectNode.put(HTTP_STATUS, ex.getHttpStatus().name());
        return objectNode;
    }

}
