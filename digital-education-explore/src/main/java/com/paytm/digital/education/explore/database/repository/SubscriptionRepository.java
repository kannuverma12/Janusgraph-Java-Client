package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Subscription findBySubscribableEntityTypeAndUserIdAndEntityId(
            SubscribableEntityType subscribableEntityType, long userId, long entityId);

    Subscription findBySubscribableEntityTypeAndUserIdAndStatusAndEntityId(
            SubscribableEntityType subscribableEntityType, long userId,
            SubscriptionStatus subscriptionStatus, long entityId);

    List<Subscription> findBySubscribableEntityTypeAndUserIdAndStatusAndEntityIdIn(
            SubscribableEntityType subscribableEntityType, long userId,
            SubscriptionStatus subscriptionStatus, List<Long> entityIds);

}
