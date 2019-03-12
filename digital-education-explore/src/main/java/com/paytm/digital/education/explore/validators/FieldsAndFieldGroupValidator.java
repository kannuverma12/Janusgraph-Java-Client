package com.paytm.digital.education.explore.validators;

import com.paytm.digital.education.explore.sro.request.FieldsAndFieldGroupRequest;
import com.paytm.digital.education.explore.annotation.FieldsAndFieldGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class FieldsAndFieldGroupValidator implements
    ConstraintValidator<FieldsAndFieldGroup, FieldsAndFieldGroupRequest> {

    private boolean isFieldsAndFieldGroupParamsValid(List<String> fields, String fieldGroup) {
        return CollectionUtils.isEmpty(fields) != StringUtils.isBlank(fieldGroup);
    }

    @Override
    public boolean isValid(
        FieldsAndFieldGroupRequest getSubscriptionRequest,
        ConstraintValidatorContext constraintValidatorContext) {
        return isFieldsAndFieldGroupParamsValid(
            getSubscriptionRequest.getFields(), getSubscriptionRequest.getFieldGroup());
    }
}
