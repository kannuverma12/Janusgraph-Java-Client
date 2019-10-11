package com.paytm.digital.education.admin.validator;

import static com.paytm.digital.education.mapping.ErrorEnum.UNAUTHORIZED_REQUEST;

import com.paytm.digital.education.exception.EducationException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImportDataValidator {

    private static final Logger log = LoggerFactory.getLogger(ImportDataValidator.class);

    @Value("${explore.ingestion.auth.token}")
    private String ingestionAuthToken;

    public void validateRequest(String token) {
        if (!this.ingestionAuthToken.equals(token)) {
            throw new EducationException(UNAUTHORIZED_REQUEST, "Invalid auth token");
        }
    }
}
