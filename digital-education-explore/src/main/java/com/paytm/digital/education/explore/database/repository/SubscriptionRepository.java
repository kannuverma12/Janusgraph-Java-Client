package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    public Subscription findByEntityAndEntityIdAndUserId(EducationEntity entity, long entityId,
            long userId);

    public List<Subscription> findByUserIdAndStatus(long userId, SubscriptionStatus status);

    public List<Subscription> findByUserIdAndEntityInAndStatus(long userId,
            List<EducationEntity> entities, SubscriptionStatus status);


}
