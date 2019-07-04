package com.paytm.digital.education.coaching.database.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@AllArgsConstructor
public class CommonCoachingMongoRepository {

    private MongoOperations mongoOperations;

    public void updateFirst(Map<String, Object> searchRequest, List<String> fields, Update update,
            Class<?> type) {
        mongoOperations.updateFirst(createMongoQuery(searchRequest, fields), update, type);
    }

    private Query createMongoQuery(Map<String, Object> searchRequest, List<String> fields) {
        Query mongoQuery = new Query();
        searchRequest.forEach((key, value) -> {
            mongoQuery.addCriteria(Criteria.where(key).is(value));
        });
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        return mongoQuery;
    }
}
