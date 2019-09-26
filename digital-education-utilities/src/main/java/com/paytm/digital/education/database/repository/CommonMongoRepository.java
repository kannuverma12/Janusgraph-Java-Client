package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.FieldGroup;
import com.paytm.digital.education.database.entity.FtlTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.ELEM_MATCH;
import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.NE;
import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.constant.DBConstants.EQ_OPERATOR;
import static com.paytm.digital.education.constant.DBConstants.GROUP_ACTIVE;
import static com.paytm.digital.education.constant.DBConstants.GROUP_ENTITY;
import static com.paytm.digital.education.constant.DBConstants.GROUP_NAME;
import static com.paytm.digital.education.constant.DBConstants.IN_OPERATOR;

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

    @Cacheable(value = "fields", unless = "#result == null")
    public <T> List<T> getEntityFieldsByValuesInAndSortBy(String key, List<Long> entityIds,
            Class<T> instance, List<String> fields, Map<Sort.Direction, String> sortMap) {
        Query mongoQuery = new Query(Criteria.where(key).in(entityIds));

        if (!CollectionUtils.isEmpty(sortMap)) {
            for (Map.Entry<Sort.Direction, String> entry : sortMap.entrySet()) {
                mongoQuery.with(new Sort(entry.getKey(), (String) entry.getValue()));
            }
        }

        fields.forEach(field -> mongoQuery.fields().include(field));
        return executeMongoQuery(mongoQuery, instance);
    }

    @Cacheable(value = "fields", unless = "#result == null")
    public <T> List<T> getEntityFieldsForCollection(Class<T> instance, List<String> fields) {
        Query mongoQuery = new Query();
        fields.forEach(field -> mongoQuery.fields().include(field));
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
                .and(GROUP_ENTITY).is(collectionName).and(
                        GROUP_ACTIVE).is(true));
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

    public void saveMultipleObject(List<Object> objects) {
        mongoOperation.insertAll(objects);
    }

    public void updateFirst(Map<String, Object> searchRequest, List<String> fields, Update update,
            Class<?> type) {
        mongoOperation.updateFirst(createMongoQuery(searchRequest, fields), update, type);
    }

    public void updateMulti(Map<String, Object> searchRequest, List<String> fields, Update update,
            Class<?> type) {
        mongoOperation.updateMulti(createMongoQuery(searchRequest, fields), update, type);
    }

    public void upsertData(Map<String, Object> searchRequest, List<String> fields, Update update,
            Class<?> type) {
        mongoOperation.upsert(createMongoQuery(searchRequest, fields), update, type);
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

    public <T> T findAndModify(Map<String, Object> searchRequest, Update update,
            FindAndModifyOptions options, Class<T> type) {

        return mongoOperation
                .findAndModify(createMongoQuery(searchRequest, new ArrayList<>()), update,
                        options,
                        type);
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
                Criteria criteria = Criteria.where(key);
                Map nestedFieldsMap = ((Map) value);
                if (nestedFieldsMap.containsKey(EXISTS)) {
                    criteria.exists((Boolean) (nestedFieldsMap.get(EXISTS)));
                }
                if (((Map) value).containsKey(ELEM_MATCH)) {
                    Criteria elemMatchCriteria = null;
                    for (Object nestedKey : nestedFieldsMap.keySet()) {
                        if (Objects.isNull(elemMatchCriteria)) {
                            elemMatchCriteria = Criteria.where(nestedKey.toString())
                                    .is(nestedFieldsMap.get(nestedKey));
                        } else {
                            elemMatchCriteria.and(nestedKey.toString())
                                    .is(nestedFieldsMap.get(nestedKey));
                        }
                    }
                    criteria.elemMatch(elemMatchCriteria);
                }
                if (nestedFieldsMap.containsKey(NE)) {
                    criteria.ne(nestedFieldsMap.get(NE));
                }
                if (nestedFieldsMap.containsKey(EQ_OPERATOR)) {
                    criteria.is(nestedFieldsMap.get(EQ_OPERATOR));
                }
                if (nestedFieldsMap.containsKey(IN_OPERATOR)) {
                    criteria.in((Collection<?>) nestedFieldsMap.get(IN_OPERATOR));
                }
                mongoQuery.addCriteria(criteria);
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
