package com.paytm.digital.education.explore.aggregation.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import com.mongodb.client.result.UpdateResult;
import com.paytm.digital.education.explore.aggregation.SubscriptionDao;
import com.paytm.digital.education.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.database.entity.Subscription;
import com.paytm.digital.education.enums.SubscribableEntityType;
import com.paytm.digital.education.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class SubscriptionDaoImpl implements SubscriptionDao {

    private final MongoTemplate mongoTemplate;

    private static final String ENTITY_DETAILS_ALIAS         = "entity_details";
    private static final String ENTITY                       = "entity";
    private static final String STATUS                       = "status";
    private static final String UPDATED_AT                   = "updated_at";
    private static final String COUNT                        = "count";
    private static final String USER_ID                      = "user_id";
    private static final String ENTITY_ID                    = "entity_id";
    private static final String SUBSCRIPTION_COLLECTION_NAME = "subscription";
    private static final String ID                           = "_id";

    private ProjectionOperation addEntitySubFieldProjection(ProjectionOperation projectionOperation,
            List<String> fields, String entityAlias) {
        for (String field : fields) {
            String entitySubField = entityAlias + "." + field;
            projectionOperation = projectionOperation.and(entitySubField).as(entitySubField);
        }
        return projectionOperation;
    }

    @Override
    public long unsubscribeUserSubscriptions(long userId,
            SubscribableEntityType subscribableEntityType, List<Long> entityIds, boolean all) {
        Query query = new Query(Criteria.where(Subscription.Constants.USER_ID).is(userId)
                .and(Subscription.Constants.ENTITY).is(subscribableEntityType)
                .and(Subscription.Constants.STATUS).is(SubscriptionStatus.SUBSCRIBED));
        if (all == false) {
            query.addCriteria(Criteria.where(Subscription.Constants.ENTITY_ID).in(entityIds));
        }

        Date currentDate = new Date();

        Update update =
                new Update().set(Subscription.Constants.STATUS, SubscriptionStatus.UNSUBSCRIBED)
                        .set(Subscription.Constants.LAST_UPDATED, currentDate);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Subscription.class);

        return updateResult.getModifiedCount();
    }

    @Override
    public List<Subscription> getUserSubscriptions(long userId,
            SubscribableEntityType subscribableEntityType,
            List<String> fields, long offset,
            long limit, SubscriptionStatus subscriptionStatus) {
        String subscriptionEntityCollectionName =
                subscribableEntityType.getCorrespondingCollectionName();
        ProjectionOperation basicProjection = project(
                ENTITY, STATUS, UPDATED_AT);
        Aggregation agg = newAggregation(
                match(new Criteria().andOperator(
                        Criteria.where(USER_ID).is(userId),
                        Criteria.where(ENTITY).is(subscribableEntityType.toString()),
                        Criteria.where(STATUS).is(subscriptionStatus)
                )),
                lookup(subscriptionEntityCollectionName, ENTITY_ID,
                        subscriptionEntityCollectionName + ID, ENTITY_DETAILS_ALIAS),
                unwind(ENTITY_DETAILS_ALIAS),
                addEntitySubFieldProjection(basicProjection, fields, ENTITY_DETAILS_ALIAS),
                skip(offset), limit(limit));

        AggregationResults groupResults
                = mongoTemplate.aggregate(
                agg, SUBSCRIPTION_COLLECTION_NAME, subscribableEntityType.getCorrespondingClass());

        return groupResults.getMappedResults();
    }

    /* TODO:- Count must verify from entity collections */
    @Override
    public List<SubscribedEntityCount> getSubscribedEntityCount(long userId,
            List<SubscribableEntityType> subscribableEntityTypes,
            SubscriptionStatus subscriptionStatus) {
        Aggregation agg = newAggregation(
                match(new Criteria().andOperator(
                        Criteria.where(USER_ID).is(userId),
                        Criteria.where(ENTITY).in(subscribableEntityTypes),
                        Criteria.where(STATUS).is(subscriptionStatus)
                )),
                group(ENTITY).count().as(COUNT),
                project(COUNT).and(ENTITY).previousOperation());
        AggregationResults groupResults
                = mongoTemplate
                .aggregate(agg, SUBSCRIPTION_COLLECTION_NAME, SubscribedEntityCount.class);
        return groupResults.getMappedResults();
    }

}
