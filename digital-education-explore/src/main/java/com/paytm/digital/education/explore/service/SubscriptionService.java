package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;

import java.util.List;

public interface SubscriptionService {

    void subscribe(long userId, SubscribableEntityType entity, long entityId);

    void unsubscribe(long userId, SubscribableEntityType entity, long entityId);

    List<Subscription> fetchSubscriptions(long userId, SubscribableEntityType subscriptionEntity,
                                          List<String> fields, String fieldGroup,
                                          long offset, long limit);

    List<SubscribedEntityCount> fetchSubscribedEntityCount(
        long userId, List<SubscribableEntityType> subscribableEntityTypes);
}
