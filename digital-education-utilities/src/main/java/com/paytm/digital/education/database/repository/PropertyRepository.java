package com.paytm.digital.education.database.repository;

import static com.paytm.digital.education.constant.DBConstants.COMPONENT;
import static com.paytm.digital.education.constant.DBConstants.KEY;
import static com.paytm.digital.education.constant.DBConstants.NAMESPACE;

import com.paytm.digital.education.database.entity.Properties;
import lombok.AllArgsConstructor;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@AllArgsConstructor
@Repository
public class PropertyRepository {

    private MongoOperations mongoOperation;
    
    public List<Properties> findByComponentAndAndNamespaceAndKeysIn(String component,
            String namespace, List<String> keys) {
        Query mongoQuery = new Query(
                Criteria.where(KEY).in(keys).and(COMPONENT).is(component).and(NAMESPACE)
                        .is(namespace));
        return mongoOperation.find(mongoQuery, Properties.class);
    }

    public List<Properties> findByComponentAndNamespace(String component,
            String namespace) {
        Query mongoQuery = new Query(
                Criteria.where(COMPONENT).is(component).and(NAMESPACE)
                        .is(namespace));
        return mongoOperation.find(mongoQuery, Properties.class);
    }

    public Properties findByComponentAndNamespaceAndKey(String component, String namespace,
            String key) {
        Query mongoQuery = new Query(
                Criteria.where(KEY).is(key).and(COMPONENT).is(component).and(NAMESPACE)
                        .is(namespace));
        return mongoOperation.findOne(mongoQuery, Properties.class);
    }

}
