package com.paytm.digital.education.daoresult.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Subscription;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionWithExam extends Subscription {
    @Field("entity_details")
    @JsonProperty("entity_details")
    private Exam entityDetails;
}
