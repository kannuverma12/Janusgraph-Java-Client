package com.paytm.digital.education.application.exception;

import com.paytm.digital.education.application.constant.ErrorCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResourceNotFoundException extends GlobalException {

    private static final long serialVersionUID = 9179933784421097052L;

    private String            resourceName;

    @Builder
    public ResourceNotFoundException(Throwable cause, ErrorCode errorCode,
            String message, String resourceName) {
        super(HttpStatus.NOT_FOUND, cause, errorCode,
                message == null ? "Resource not found: " + resourceName : message);
        this.resourceName = resourceName;
    }
}
