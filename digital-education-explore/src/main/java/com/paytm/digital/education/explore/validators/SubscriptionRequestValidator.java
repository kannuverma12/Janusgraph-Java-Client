package com.paytm.digital.education.explore.validators;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;
import com.paytm.digital.education.mapping.ErrorEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Service
public class SubscriptionRequestValidator {

    public void validate(SubscriptionRequest request) {
        if (Objects.isNull(request.getSubscriptionEntityId()) && CollectionUtils
                .isEmpty(request.getSubscriptionEntityIds()) && !request.isAll()) {
            throw new BadRequestException(ErrorEnum.ENTITY_ID_MANDATORY_FOR_SUBSCRIPTION,
                    ErrorEnum.ENTITY_ID_MANDATORY_FOR_SUBSCRIPTION.getExternalMessage());
        }
    }

}
