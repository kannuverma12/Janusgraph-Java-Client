package com.paytm.digital.education.explore.sro.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Data
@Getter
@Setter
public class SubscriptionRequest {

    @JsonProperty("entity")
    private EducationEntity subscriptionEntity;

    @JsonProperty("entity_id")
    private long subscriptionEntityId;

    @JsonProperty("user_id")
    private long userId;
}
