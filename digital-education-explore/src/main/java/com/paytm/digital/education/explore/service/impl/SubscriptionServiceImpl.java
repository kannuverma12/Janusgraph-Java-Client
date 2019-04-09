package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.mapping.ErrorEnum.ENTITY_NOT_SUBSCRIBED;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPROVALS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_SEARCH_NAMESPACE;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.aggregation.SubscriptionDao;
import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithInstitute;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import com.paytm.digital.education.explore.service.CommonMongoService;
import com.paytm.digital.education.explore.service.SubscriptionService;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private SubscriptionRepository subscriptionRepository;

    private SubscriptionDao subscriptionDao;

    private CommonMongoService commonMongoService;

    private PropertyReader propertyReader;

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

        if (subscriptionObj == null) {
            throw new BadRequestException(ENTITY_NOT_SUBSCRIBED,
                    ENTITY_NOT_SUBSCRIBED.getExternalMessage());
        }
        if (SubscriptionStatus.SUBSCRIBED.equals(subscriptionObj.getStatus())) {
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
        Map<String, Map<String, Object>> propertyMap = propertyReader
                .getPropertiesAsMap(EXPLORE_COMPONENT, INSTITUTE_SEARCH_NAMESPACE);

        for (Subscription subscription : subscriptions) {
            updateValues(subscription, subscriptionEntity, propertyMap);
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

    // TODO - add warning and entityId and entityType in case of missing logo or exception
    private void updateValues(Subscription subscription,
            SubscribableEntityType subscriptionEntity,
            Map<String, Map<String, Object>> propertyMap) {
        try {
            if (subscriptionEntity.getCorrespondingClass() == SubscriptionWithInstitute.class) {
                SubscriptionWithInstitute subscriptionWithInstitute =
                        (SubscriptionWithInstitute) subscription;
                List<String> formattedValues = CommonUtil.formatValues(propertyMap, APPROVALS,
                        subscriptionWithInstitute.getEntityDetails().getApprovals());
                subscriptionWithInstitute.getEntityDetails().setApprovals(formattedValues);
                String logoLink = CommonUtil.getLogoLink(
                        subscriptionWithInstitute.getEntityDetails().getGallery().getLogo());
                if (StringUtils.isNotBlank(logoLink)) {
                    subscriptionWithInstitute.getEntityDetails().getGallery()
                            .setLogo(logoLink);
                }
            }
        } catch (Exception ex) {
            log.error(
                    "Error caught while formatting values in subscription data for . Exception : ",
                    ex);
        }
    }
}
