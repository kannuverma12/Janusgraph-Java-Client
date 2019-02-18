package com.paytm.digital.education.exception;

import com.paytm.digital.education.constant.ErrorCode;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.NOT_FOUND)
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
