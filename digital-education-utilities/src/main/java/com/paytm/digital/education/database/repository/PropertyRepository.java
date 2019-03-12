package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Properties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Repository
public class PropertyRepository {

    private MongoOperations mongoOperation;

    public List<Properties> findByComponentAndAndNamespaceAndKeysIn(String component,
            String namespace, List<String> keys) {
        Query mongoQuery = new Query(
                Criteria.where("key").in(keys).and("component").is(component).and("namespace")
                        .is(namespace));
        return mongoOperation.find(mongoQuery, Properties.class);
    }

    public Properties findByComponentAndNamespaceAndKey(String component, String namespace,
            String key) {
        Query mongoQuery = new Query(
                Criteria.where("key").is(key).and("component").is(component).and("namespace")
                        .is(namespace));
        return mongoOperation.findOne(mongoQuery, Properties.class);
    }

}
