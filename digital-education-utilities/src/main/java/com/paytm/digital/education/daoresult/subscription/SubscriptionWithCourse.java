package com.paytm.digital.education.daoresult.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Subscription;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionWithCourse extends Subscription {
    @Field("entity_details")
    @JsonProperty("entity_details")
    private Course entityDetails;
}
