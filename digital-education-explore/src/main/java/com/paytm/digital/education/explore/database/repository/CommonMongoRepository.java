package com.paytm.digital.education.explore.database.repository;

import static com.paytm.digital.education.explore.constants.ExploreConstants.GROUP_ACTIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GROUP_ENTITY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GROUP_NAME;
import com.paytm.digital.education.explore.database.entity.FieldGroup;
import com.paytm.digital.education.explore.database.entity.FtlTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
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

    public <T> List<T> getEntityFieldsByValuesIn(String key, List<Long> entityIds,
            Class<T> instance,
            List<String> fields) {
        Query mongoQuery = new Query(Criteria.where(key).in(entityIds));
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return executeMongoQuery(mongoQuery, instance);
    }

    public <T> List<String> getFieldsByGroup(Class<T> collectionClass, String fieldGroup) {
        String collectionName = context.getPersistentEntity(collectionClass).getCollection();
        return getFieldsByGroupAndCollectioName(collectionName, fieldGroup);
    }

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
}
