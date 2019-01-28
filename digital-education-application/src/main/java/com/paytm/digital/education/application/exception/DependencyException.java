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
public class DependencyException extends GlobalException {

    private static final long serialVersionUID = 1985919214044846535L;

    private String            dependencyName;

    @Builder
    public DependencyException(Throwable cause, ErrorCode errorCode, String message, String dependencyName) {
        super(HttpStatus.FAILED_DEPENDENCY, cause, errorCode,
                message == null ? "Failed Dependency: " + dependencyName : message);
        this.dependencyName = dependencyName;
    }

}
