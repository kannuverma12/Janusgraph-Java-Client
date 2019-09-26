package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.FailedData;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Repository
public class FailedDataRepository {
    private MongoOperations mongoOperation;

    public void saveAll(List<Object> failedDataList) {
        mongoOperation.insertAll(failedDataList);
    }

    public List<FailedData> findAll(Map<String, Object> searchRequest) {
        Query mongoQuery = new Query();
        searchRequest.forEach((key, value) -> {
            mongoQuery.addCriteria(Criteria.where(key).is(value));
        });
        return mongoOperation.find(mongoQuery, FailedData.class);
    }

    public void updateMulti(Map<String, Object> searchRequest, List<String> fields, Update update) {
        Query mongoQuery = new Query();
        searchRequest.forEach((key, value) -> {
            mongoQuery.addCriteria(Criteria.where(key).is(value));
        });
        fields.forEach(field -> {
            mongoQuery.fields().include(field);
        });
        mongoOperation.updateMulti(mongoQuery, update, FailedData.class);
    }
}
