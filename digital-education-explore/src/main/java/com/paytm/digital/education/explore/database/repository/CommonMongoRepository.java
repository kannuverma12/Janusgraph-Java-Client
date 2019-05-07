package com.paytm.digital.education.explore.database.repository;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.ELEM_MATCH;
import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.NE;
import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EQ_OPERATOR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GROUP_ACTIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GROUP_ENTITY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GROUP_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IN_OPERATOR;

import com.paytm.digital.education.explore.database.entity.FieldGroup;
import com.paytm.digital.education.explore.database.entity.FtlTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Repository
public class CommonMongoRepository {

    private MongoOperations     mongoOperation;
    private MongoMappingContext context;

    @Cacheable(value = "entities", unless = "#result == null")
    public <T> T getEntityById(String key, long entityId, Class<T> instance) {
        log.debug("Querying entityById for key :  {}, entityId : {}", key, entityId);
        Query mongoQuery = new Query(Criteria.where(key).is(entityId));
        return executeQuery(mongoQuery, instance);
    }

    @Cacheable(value = "fields", unless = "#result == null")
    public <T> T getEntityByFields(String key, long entityId, Class<T> instance,
            List<String> fields) {
        Query mongoQuery = new Query(Criteria.where(key).is(entityId));
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return executeQuery(mongoQuery, instance);
    }

    @Cacheable(value = "fields", unless = "#result == null")
    public <T> List<T> getEntityFieldsByValuesIn(String key, List<Long> entityIds,
            Class<T> instance,
            List<String> fields) {
        Query mongoQuery = new Query(Criteria.where(key).in(entityIds));
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return executeMongoQuery(mongoQuery, instance);
    }

    @Cacheable(value = "field_group", unless = "#result == null")
    public <T> List<String> getFieldsByGroup(Class<T> collectionClass, String fieldGroup) {
        String collectionName = context.getPersistentEntity(collectionClass).getCollection();
        return getFieldsByGroupAndCollectioName(collectionName, fieldGroup);
    }

    @Cacheable(value = "field_group", unless = "#result == null")
    public List<String> getFieldsByGroupAndCollectioName(String collectionName, String fieldGroup) {
        Query mongoQuery = new Query(Criteria
                .where(GROUP_NAME).is(fieldGroup)
                .and(GROUP_ENTITY).is(collectionName).and(GROUP_ACTIVE).is(true));
        FieldGroup groupDetail = executeQuery(mongoQuery, FieldGroup.class);
        if (groupDetail != null) {
            return groupDetail.getFields();
        }
        return null;

    }

    @Cacheable(value = "entities", unless = "#result == null")
    public <T> List<T> getEntitiesByIdAndFields(String key, long entityId, Class<T> type,
            List<String> fields) {
        Query mongoQuery = new Query(Criteria.where(key).is(entityId));
        if (!CollectionUtils.isEmpty(fields)) {
            fields.forEach(field -> {
                mongoQuery.fields().include(field);
            });
        }
        return executeMongoQuery(mongoQuery, type);
    }

    @Cacheable(value = "ftl_templates", unless = "#result == null")
    public String getTemplate(String templateName, String entityName) {
        Query mongoQuery = new Query(
                Criteria.where("name").is(templateName).and("entity").is(entityName).and("active")
                        .is(true));
        FtlTemplate template = executeQuery(mongoQuery, FtlTemplate.class);
        if (template != null) {
            return template.getTemplate();
        }
        return null;
    }

    public void saveOrUpdate(Object obj) {
        mongoOperation.save(obj);
    }

    private <T> T executeQuery(Query mongoQuery, Class<T> type) {
        return mongoOperation.findOne(mongoQuery, type);
    }

    private <T> List<T> executeMongoQuery(Query mongoQuery, Class<T> type) {
        return mongoOperation.find(mongoQuery, type);
    }

    private <T> List<T> executeMongoQueryDistinct(Query mongoQuery, String field, Class<?> type,
            Class<T> result) {
        return mongoOperation.findDistinct(mongoQuery, field, type, result);
    }

    public <T> List<T> findAll(Map<String, Object> searchRequest, Class<T> instance,
            List<String> fields, String queryOperatorType) {
        if (queryOperatorType.equals(AND)) {
            return executeMongoQuery(createMongoQuery(searchRequest, fields), instance);
        } else if (queryOperatorType.equals(OR)) {
            return executeMongoQuery(createOrMongoQuery(searchRequest, fields), instance);
        }
        return null;
    }

    public <T> List<T> findAllDistinctValues(Map<String, Object> searchRequest, Class<?> instance,
            String field, Class<T> result) {
        return executeMongoQueryDistinct(createMongoQuery(searchRequest, new ArrayList<>()),
                field, instance, result);
    }

    /*
     ** This will generate a query object based on the params passed
     ** Params :
     *        searchRequest : map of field and its value for the where clause
     *        fields : list of projection fields
     *        it uses and operator
     */
    private Query createMongoQuery(Map<String, Object> searchRequest, List<String> fields) {
        Query mongoQuery = new Query();
        searchRequest.forEach((key, value) -> {
            if (value instanceof Map) {
                Map nestedMap = ((Map) value);
                if (nestedMap.containsKey(EXISTS)) {
                    mongoQuery.addCriteria(Criteria.where(key).exists((Boolean) (nestedMap.get(
                            EXISTS))));
                }
                if (((Map) value).containsKey(ELEM_MATCH)) {
                    Criteria elemMatchCriteria = null;
                    for (Object nestedKey : nestedMap.keySet()) {
                        if (Objects.isNull(elemMatchCriteria)) {
                            elemMatchCriteria = Criteria.where(nestedKey.toString()).is(nestedMap.get(nestedKey));
                        } else {
                            elemMatchCriteria.and(nestedKey.toString()).is(nestedMap.get(nestedKey));
                        }
                    }
                    mongoQuery.addCriteria(Criteria.where(key).elemMatch(elemMatchCriteria));
                }
                if (nestedMap.containsKey(NE)) {
                    mongoQuery.addCriteria(Criteria.where(key).ne(nestedMap.get(NE)));
                }
                if (nestedMap.containsKey(EQ_OPERATOR)) {
                    mongoQuery.addCriteria(Criteria.where(key).is(nestedMap.get(EQ_OPERATOR)));
                }
                if (nestedMap.containsKey(IN_OPERATOR)) {
                    mongoQuery.addCriteria(Criteria.where(key).in(nestedMap.get(IN_OPERATOR)));
                }
            } else {
                mongoQuery.addCriteria(Criteria.where(key).is(value));
            }
        });
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return mongoQuery;
    }

    /*
     ** This will generate a query object based on the params passed
     ** Params :
     *        searchRequest : map of field and its value for the where clause
     *        fields : list of projection fields
     *        it uses or operator
     */
    private Query createOrMongoQuery(Map<String, Object> searchRequest, List<String> fields) {
        Query mongoQuery = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : searchRequest.entrySet()) {
            criteriaList.add(Criteria.where(entry.getKey()).in((ArrayList) entry.getValue()));
        }
        mongoQuery.addCriteria(
                new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return mongoQuery;
    }
}
