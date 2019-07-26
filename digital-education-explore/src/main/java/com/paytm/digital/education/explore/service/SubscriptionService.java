package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.dto.NotificationFlags;
import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;

import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;

import java.util.List;

public interface SubscriptionService {

    NotificationFlags subscribe(long userId, SubscribableEntityType entity, long entityId);

    NotificationFlags unsubscribe(long userId, SubscriptionRequest subscriptionRequest);

    List<Subscription> fetchSubscriptions(long userId, SubscribableEntityType subscriptionEntity,
                                          List<String> fields, String fieldGroup,
                                          long offset, long limit, SubscriptionStatus subscriptionStatus);

    List<SubscribedEntityCount> fetchSubscribedEntityCount(
        long userId, List<SubscribableEntityType> subscribableEntityTypes, SubscriptionStatus subscriptionStatus);
}
