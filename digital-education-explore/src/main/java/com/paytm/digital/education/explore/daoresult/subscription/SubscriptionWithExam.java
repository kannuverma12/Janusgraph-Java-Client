package com.paytm.digital.education.explore.daoresult.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Subscription;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionWithExam extends Subscription {
    @Field("entity_details")
    @JsonProperty("entity_details")
    private Exam entityDetails;
}
