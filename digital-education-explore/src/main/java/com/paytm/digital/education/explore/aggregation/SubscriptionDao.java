package com.paytm.digital.education.explore.aggregation;

import com.paytm.digital.education.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.database.entity.Subscription;
import com.paytm.digital.education.enums.SubscribableEntityType;

import com.paytm.digital.education.enums.SubscriptionStatus;
import java.util.List;

public interface SubscriptionDao {
    List<SubscribedEntityCount> getSubscribedEntityCount(long userId,
                                                         List<SubscribableEntityType> subscribableEntityTypes,
                                                         SubscriptionStatus subscriptionStatus);

    List<Subscription> getUserSubscriptions(long userId, SubscribableEntityType entity, List<String> fields,
                                            long offset, long limit, SubscriptionStatus subscriptionStatus);

    long unsubscribeUserSubscriptions(long userId,
            SubscribableEntityType subscribableEntityType, List<Long> entityIds, boolean all);
}
