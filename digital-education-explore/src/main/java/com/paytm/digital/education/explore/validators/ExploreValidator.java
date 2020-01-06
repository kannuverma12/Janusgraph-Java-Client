package com.paytm.digital.education.explore.validators;

import com.paytm.digital.education.exception.ValidationException;
import com.paytm.digital.education.explore.request.dto.search.CutoffSearchRequest;
import com.paytm.digital.education.explore.sro.request.FieldsAndFieldGroupRequest;
import com.paytm.digital.education.explore.sro.request.FetchSubscriptionsRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ExploreValidator {

    private final Validator validator;

    public void validateAndThrowException(Object o) {
        Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if (!violations.isEmpty()) {
            throw ValidationException.buildValidationExceptionForObject(violations);
        }
    }

    public void validateFetchSubscriptionRequest(FetchSubscriptionsRequest fetchSubscriptionsRequest) {
        validateAndThrowException(fetchSubscriptionsRequest);
    }

    public void validateFieldAndFieldGroup(List<String> fields, String fieldGroup) {
        FieldsAndFieldGroupRequest fieldsAndFieldGroupRequest = new FieldsAndFieldGroupRequest(fields, fieldGroup);
        validateAndThrowException(fieldsAndFieldGroupRequest);
    }

    public void validateCutOffSearchRequest(CutoffSearchRequest cutoffSearchRequest) {
        validateAndThrowException(cutoffSearchRequest);
    }
}
