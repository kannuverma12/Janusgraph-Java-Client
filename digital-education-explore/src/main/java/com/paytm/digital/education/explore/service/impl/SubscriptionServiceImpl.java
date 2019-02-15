package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.aggregation.SubscriptionDao;
import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;

import com.paytm.digital.education.explore.service.CommonMongoService;
import com.paytm.digital.education.explore.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private SubscriptionRepository subscriptionRepository;

    private SubscriptionDao subscriptionDao;

    private CommonMongoService commonMongoService;

    @Override
    public void subscribe(long userId, SubscribableEntityType entity, long entityId) {
        Date currentDate = new java.util.Date();
        Subscription subscriptionObj =
            subscriptionRepository.findBySubscribableEntityTypeAndEntityIdAndUserId(
                entity, entityId, userId);

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

    @Override
    public void unsubscribe(long userId, SubscribableEntityType entity, long entityId) {
        Date currentDate = new java.util.Date();

        Subscription subscriptionObj =
            subscriptionRepository.findBySubscribableEntityTypeAndEntityIdAndUserId(
                entity, entityId, userId);

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

    @Override
    public List<Subscription> fetchSubscriptions(long userId, SubscribableEntityType subscriptionEntity,
                                                List<String> fields, String fieldGroup, long offset, long limit) {
        List<String> toBeFetchedFieldList = StringUtils.isEmpty(fieldGroup)
            ? fields :
            commonMongoService.getFieldsByGroupAndCollectioName(
                subscriptionEntity.getCorrespondingCollectionName(), fieldGroup);

        return subscriptionDao.getUserSubscriptions(
            userId, subscriptionEntity, toBeFetchedFieldList, offset, limit);
    }

    @Override
    public List<SubscribedEntityCount> fetchSubscribedEntityCount(
        long userId, List<SubscribableEntityType> subscribableEntityTypes) {
        return subscriptionDao.getSubscribedEntityCount(userId, subscribableEntityTypes);
    }

    @Override
    public boolean isFieldsAndFieldGroupParamsInvalid(List<String> fields, String fieldGroup) {
        return CollectionUtils.isEmpty(fields) == StringUtils.isEmpty(fieldGroup);
    }

}
