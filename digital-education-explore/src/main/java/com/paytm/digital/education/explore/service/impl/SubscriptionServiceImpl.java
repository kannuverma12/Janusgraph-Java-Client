package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles all the business logic related to subscriptions.
 * TODO: Implement `UPSERT` - instead of handling on application level, move it to mongo repo
 * 
 * @author himanshujain
 */

@Service
public class SubscriptionServiceImpl {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public void subscribe(long userId, EducationEntity entity, long entityId) {
        Date currentDate = new java.util.Date();
        Subscription subscriptionObj =
                subscriptionRepository.findByEntityAndEntityIdAndUserId(entity, entityId, userId);

        if (subscriptionObj == null) {
            subscriptionRepository.save(new Subscription(userId, entity,
                    entityId, SubscriptionStatus.SUBSCRIBED, currentDate,
                    currentDate));
        } else if (!subscriptionObj.getStatus().equals(SubscriptionStatus.SUBSCRIBED)) {
            subscriptionObj.setStatus(SubscriptionStatus.SUBSCRIBED);
            subscriptionObj.setLastModified(currentDate);
            subscriptionRepository.save(subscriptionObj);
        }
    }


    public void unsubscribe(long userId, EducationEntity entity, long entityId) {
        Date currentDate = new java.util.Date();

        Subscription subscriptionObj =
                subscriptionRepository.findByEntityAndEntityIdAndUserId(entity, entityId, userId);

        if (subscriptionObj == null) {
            subscriptionRepository.save(new Subscription(userId, entity,
                    entityId, SubscriptionStatus.UNSUBSCRIBED, currentDate,
                    currentDate));
        } else if (!subscriptionObj.getStatus().equals(SubscriptionStatus.UNSUBSCRIBED)) {
            subscriptionObj.setStatus(SubscriptionStatus.UNSUBSCRIBED);
            subscriptionObj.setLastModified(currentDate);
            subscriptionRepository.save(subscriptionObj);
        }
    }

    public List<Subscription> fetchSubscriptionList(long userId,
            List<EducationEntity> subscriptionEntitiesList) {
        List<Subscription> subscribedItems = new ArrayList<>();
        if (subscriptionEntitiesList == null || subscriptionEntitiesList.size() == 0) {
            subscribedItems =
                    subscriptionRepository.findByUserIdAndStatus(userId,
                            SubscriptionStatus.SUBSCRIBED);
        } else {
            subscribedItems = subscriptionRepository.findByUserIdAndEntityInAndStatus(userId,
                    subscriptionEntitiesList, SubscriptionStatus.SUBSCRIBED);
        }
        return subscribedItems;
    }

}
