package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.SubscribableEntityType;
import com.paytm.digital.education.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubscriptionDetailHelper {

    private SubscriptionRepository subscriptionRepository;

    public List<Long> getSubscribedEntities(EducationEntity educationEntity, Long userId, Collection<Long> entityIds) {
        SubscribableEntityType subscribableEntityType = EducationEntity.convertToSubscribableEntity(educationEntity);
        List<Subscription> subscribedEntities = subscriptionRepository
                .findBySubscribableEntityTypeAndUserIdAndStatusAndEntityIdIn(subscribableEntityType, userId,
                        SubscriptionStatus.SUBSCRIBED, entityIds);
        if (!CollectionUtils.isEmpty(subscribedEntities)) {
            return subscribedEntities.stream().map(subscription -> subscription.getEntityId())
                    .collect(Collectors.toList());
        }
        return null;
    }
    
}
