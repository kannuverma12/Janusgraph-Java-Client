package com.paytm.digital.education.explore.daoresult.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.entity.Subscription;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionWithSchool extends Subscription {
    @Field("entity_details")
    @JsonProperty("entity_details")
    private School entityDetails;
}
