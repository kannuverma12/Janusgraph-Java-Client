package com.paytm.digital.education.explore.aggregation;

import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;

import java.util.List;

public interface SubscriptionDao {
    List<SubscribedEntityCount> getSubscribedEntityCount(long userId,
                                                         List<SubscribableEntityType> subscribableEntityTypes);

    List<Subscription> getUserSubscriptions(long userId, SubscribableEntityType entity, List<String> fields,
                                            long offset, long limit);
}