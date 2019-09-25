package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.DBConstants.UNREAD_SHORTLIST_COUNT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPROVALS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_SEARCH_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SUCCESS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;

import com.paytm.digital.education.database.entity.UserFlags;
import com.paytm.digital.education.database.repository.UserFlagRepository;
import com.paytm.digital.education.dto.NotificationFlags;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.aggregation.SubscriptionDao;
import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithInstitute;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import com.paytm.digital.education.explore.service.CommonMongoService;
import com.paytm.digital.education.explore.service.SubscriptionService;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Date;


@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private static Logger log = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private SubscriptionRepository subscriptionRepository;

    private SubscriptionDao subscriptionDao;

    private CommonMongoService commonMongoService;

    private PropertyReader     propertyReader;
    private UserFlagRepository userFlagRepository;

    private static NotificationFlags DEFAULT_SUCCESS_MESSAGE = new NotificationFlags(SUCCESS);

    @Override
    public NotificationFlags subscribe(long userId, SubscribableEntityType entity, long entityId) {
        Date currentDate = new java.util.Date();
        Subscription subscriptionObj =
                subscriptionRepository.findBySubscribableEntityTypeAndUserIdAndEntityId(
                        entity, userId, entityId);

        if (subscriptionObj == null) {
            subscriptionRepository.save(new Subscription(userId, entity,
                    entityId, SubscriptionStatus.SUBSCRIBED, currentDate,
                    currentDate));
            return createOrUpdateUserFlag(userId);
        } else if (!subscriptionObj.getStatus().equals(SubscriptionStatus.SUBSCRIBED)) {
            subscriptionObj.setStatus(SubscriptionStatus.SUBSCRIBED);
            subscriptionObj.setLastModified(currentDate);
            subscriptionRepository.save(subscriptionObj);
            return createOrUpdateUserFlag(userId);
        }
        return DEFAULT_SUCCESS_MESSAGE;
    }

    @Override
    public NotificationFlags unsubscribe(long userId, SubscriptionRequest subscriptionRequest) {

        List<Long> subscribedEntities = null;
        if (!CollectionUtils.isEmpty(subscriptionRequest.getSubscriptionEntityIds())) {
            subscribedEntities = subscriptionRequest.getSubscriptionEntityIds();
        } else {
            subscribedEntities = Arrays.asList(subscriptionRequest.getSubscriptionEntityId());
        }

        long updatedCount = subscriptionDao
                .unsubscribeUserSubscriptions(userId, subscriptionRequest.getSubscriptionEntity(),
                        subscribedEntities, subscriptionRequest.isAll());


        log.info("Unsubscribe Response : {}", DEFAULT_SUCCESS_MESSAGE);
        return decrementShortlistCount(userId, (int) updatedCount);
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
        if (Objects.isNull(toBeFetchedFieldList)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
        List<Subscription> subscriptions = subscriptionDao.getUserSubscriptions(
                userId, subscriptionEntity, toBeFetchedFieldList, offset, limit,
                subscriptionStatus);
        Map<String, Map<String, Object>> propertyMap = propertyReader
                .getPropertiesAsMap(EXPLORE_COMPONENT, INSTITUTE_SEARCH_NAMESPACE);

        for (Subscription subscription : subscriptions) {
            updateValues(subscription, subscriptionEntity, propertyMap);
        }
        resetUnreadShortlistFlag(userId);
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
                subscriptionWithInstitute.getEntityDetails().setUrlDisplayKey(CommonUtil
                        .convertNameToUrlDisplayName(
                                subscriptionWithInstitute.getEntityDetails().getOfficialName()));
                String logoLink = CommonUtil.getLogoLink(
                        subscriptionWithInstitute.getEntityDetails().getGallery().getLogo(),
                        EducationEntity.INSTITUTE);
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

    private void resetUnreadShortlistFlag(Long userId) {
        userFlagRepository.updateCounter(userId, UNREAD_SHORTLIST_COUNT, 0);
    }

    private NotificationFlags createOrUpdateUserFlag(long userId) {
        NotificationFlags notificationFlags = new NotificationFlags(SUCCESS);
        UserFlags userFlags =
                userFlagRepository.incrementCounter(userId, UNREAD_SHORTLIST_COUNT, 1);
        if (userFlags == null) {
            userFlags = new UserFlags();
            userFlags.setUserId(userId);
            userFlags.setUnreadShortlistCount(1);
            userFlagRepository.saveOrUpdate(userFlags);
            notificationFlags.setFirstShortlist(1);
        } else if (userFlags.getUnreadShortlistCount() == null) {
            userFlags.setUnreadShortlistCount(1);
            notificationFlags.setFirstShortlist(1);
            userFlagRepository.saveOrUpdate(userFlags);
        }
        notificationFlags.setUnreadShortlist(1);
        return notificationFlags;
    }

    private NotificationFlags decrementShortlistCount(long userId, int counterValue) {
        UserFlags userFlags =
                userFlagRepository
                        .decrementCounterIfPositive(userId, UNREAD_SHORTLIST_COUNT, counterValue);
        NotificationFlags notificationFlags = new NotificationFlags(SUCCESS);
        if (userFlags != null) {
            notificationFlags.setUnreadShortlist(userFlags.getShortlistFlag());
        } else {
            notificationFlags.setUnreadShortlist(0);
        }
        return notificationFlags;
    }
}
