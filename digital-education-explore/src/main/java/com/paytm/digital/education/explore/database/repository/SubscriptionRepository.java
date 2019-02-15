package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Subscription findBySubscribableEntityTypeAndEntityIdAndUserId(
        SubscribableEntityType subscribableEntityType, long entityId, long userId);

}
