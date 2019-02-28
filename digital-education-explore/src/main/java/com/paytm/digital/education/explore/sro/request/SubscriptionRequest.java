package com.paytm.digital.education.explore.sro.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ToString
@Data
@Getter
@Setter
public class SubscriptionRequest {

    @JsonProperty("entity")
    @NotNull
    private SubscribableEntityType subscriptionEntity;

    @JsonProperty("entity_id")
    @Min(1)
    private long subscriptionEntityId;

}
