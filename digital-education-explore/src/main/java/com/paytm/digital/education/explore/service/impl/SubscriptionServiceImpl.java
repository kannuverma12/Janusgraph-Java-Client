package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.aggregation.SubscriptionDao;
import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithInstitute;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import com.paytm.digital.education.explore.service.CommonMongoService;
import com.paytm.digital.education.explore.service.SubscriptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private SubscriptionRepository subscriptionRepository;

    private SubscriptionDao subscriptionDao;

    private CommonMongoService commonMongoService;

    private static String logoUrlPrefix;

    @Value("${institute.gallery.image.prefix}")
    public void setLogoUrlPrefix(String urlPrefix) {
        logoUrlPrefix = urlPrefix;
    }

    @Override
    public void subscribe(long userId, SubscribableEntityType entity, long entityId) {
        Date currentDate = new java.util.Date();
        Subscription subscriptionObj =
                subscriptionRepository.findBySubscribableEntityTypeAndUserIdAndEntityId(
                        entity, userId, entityId);

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
                subscriptionRepository.findBySubscribableEntityTypeAndUserIdAndEntityId(
                        entity, userId, entityId);

        if (subscriptionObj != null && subscriptionObj.getStatus()
                .equals(SubscriptionStatus.SUBSCRIBED)) {
            subscriptionObj.setStatus(SubscriptionStatus.UNSUBSCRIBED);
            subscriptionObj.setLastModified(currentDate);
            subscriptionRepository.save(subscriptionObj);
        }
    }

    @Override
    public List<Subscription> fetchSubscriptions(long userId,
            SubscribableEntityType subscriptionEntity,
            List<String> fields, String fieldGroup, long offset, long limit,
            SubscriptionStatus subscriptionStatus) {
        List<String> toBeFetchedFieldList = StringUtils.isEmpty(fieldGroup)
                ? fields
                : commonMongoService.getFieldsByGroupAndCollectioName(
                subscriptionEntity.getCorrespondingCollectionName(), fieldGroup);

        List<Subscription> subscriptions = subscriptionDao.getUserSubscriptions(
                userId, subscriptionEntity, toBeFetchedFieldList, offset, limit,
                subscriptionStatus);

        for (Subscription subscription : subscriptions) {
            updateInstituteLogoUrl(subscription, subscriptionEntity);
        }
        return subscriptions;
    }

    @Override
    public List<SubscribedEntityCount> fetchSubscribedEntityCount(
            long userId, List<SubscribableEntityType> subscribableEntityTypes,
            SubscriptionStatus subscriptionStatus) {
        return subscriptionDao
                .getSubscribedEntityCount(userId, subscribableEntityTypes, subscriptionStatus);
    }

    //TODO - add warning and entityId and entityType in case of missing logo or exception
    private void updateInstituteLogoUrl(Subscription subscription, SubscribableEntityType subscriptionEntity) {
        try {
            if (subscriptionEntity.getCorrespondingClass() == SubscriptionWithInstitute.class) {
                SubscriptionWithInstitute subscriptionWithInstitute = (SubscriptionWithInstitute) subscription;
                String logoUrl = subscriptionWithInstitute.getEntityDetails().getGallery().getLogo();
                if (StringUtils.isNotBlank(logoUrl)) {
                    subscriptionWithInstitute.getEntityDetails().getGallery().setLogo(logoUrlPrefix + logoUrl);
                }
            }
        } catch (Exception ex) {
            log.error("Error caught while setting logo url in subscription data for . Exception : ", ex);
        }
    }
}
