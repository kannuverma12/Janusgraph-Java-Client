package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.FieldGroup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Repository
public class CommonMongoRepository {

    private MongoOperations     mongoOperation;
    private MongoMappingContext context;

    public <T> T getEntityById(String key, long entityId, Class<T> instance) {
        log.debug("Querying entityById for key :  {}, entityId : {}", key, entityId);
        Query mongoQuery = new Query(Criteria.where(key).is(entityId));
        return executeQuery(mongoQuery, instance);
    }

    public <T> T getEntityByFields(String key, long entityId, Class<T> instance,
            List<String> fields) {
        Query mongoQuery = new Query(Criteria.where(key).is(entityId));
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return executeQuery(mongoQuery, instance);
    }

    public <T> List<String> getFieldsByGroup(Class<T> collectionClass, String fieldGroup) {
        String collectionName = context.getPersistentEntity(collectionClass).getCollection();
        return getFieldsByGroupAndCollectioName(collectionName, fieldGroup);
    }

    public List<String> getFieldsByGroupAndCollectioName(String collectionName, String fieldGroup) {
        Query mongoQuery = new Query(Criteria
            .where("name").is(fieldGroup)
            .and("entity").is(collectionName));
        FieldGroup groupDetail = executeQuery(mongoQuery, FieldGroup.class);
        return groupDetail.getFields();
    }

    private <T> T executeQuery(Query mongoQuery, Class<T> type) {
        T entity = mongoOperation.findOne(mongoQuery, type);
        return entity;
    }
}
