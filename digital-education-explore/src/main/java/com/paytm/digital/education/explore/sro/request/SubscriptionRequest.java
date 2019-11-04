package com.paytm.digital.education.explore.sro.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.SubscribableEntityType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Data
@Getter
@Setter
public class SubscriptionRequest {

    @JsonProperty("entity")
    @NotNull
    private SubscribableEntityType subscriptionEntity;

    @JsonProperty("entity_id")
    private Long subscriptionEntityId;

    @JsonProperty("entity_ids")
    private List<Long> subscriptionEntityIds;

    @JsonProperty("all")
    private boolean all;

}
