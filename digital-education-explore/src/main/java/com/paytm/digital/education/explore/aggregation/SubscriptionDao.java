package com.paytm.digital.education.explore.aggregation;

import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;

import com.paytm.digital.education.explore.enums.SubscriptionStatus;
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
