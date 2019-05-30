package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.MerchantConfiguration;
import com.paytm.digital.education.form.service.MerchantConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@Service
public class MerchantConfigServiceImpl implements MerchantConfigService {

    private MongoOperations mongoOperations;
    private MongoTemplate mongoTemplate;

    @Override
    public MerchantConfiguration getMerchantById(String merchantId, ArrayList<String> keys) {
        MerchantConfiguration merchantConfiguration = null;

        Query query = new Query(Criteria.where("_id").is(merchantId));
        keys.forEach(key -> query.fields().include(key));
        merchantConfiguration = mongoOperations
                .findOne(query, MerchantConfiguration.class);

        return merchantConfiguration;
    }

    @Override
    public void saveOrUpdateMerchantConfiguration(MerchantConfiguration merchantConfiguration) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(merchantConfiguration.getMerchantId()));

        MerchantConfiguration foundMerchantConfiguration = mongoOperations.findOne(query, MerchantConfiguration.class);

        // todo: use mongo level upsert, rather than get and update, not thread safe
        if (foundMerchantConfiguration == null) {
            merchantConfiguration.setUpdatedDate(new Date());
            merchantConfiguration.setCreatedDate(new Date());

            mongoOperations.insert(merchantConfiguration);
        } else {
            Map<String, Object> map = null;
            map = foundMerchantConfiguration.getData() != null ? foundMerchantConfiguration.getData() : new HashMap<>();
            map.putAll(merchantConfiguration.getData());
            foundMerchantConfiguration.setData(map);

            foundMerchantConfiguration.setUpdatedDate(new Date());

            Document dbDoc = new Document();
            mongoTemplate.getConverter().write(foundMerchantConfiguration, dbDoc);
            Update updateQuery = Update.fromDocument(dbDoc);

            mongoOperations.upsert(query, updateQuery, MerchantConfiguration.class);
        }
    }

    @Override
    public Map<String, Object> getPostScreenData(String merchantId, Long orderId) {
        Map<String, Object> data = null;
        if (merchantId != null) {
            data = getScreenConfig(merchantId);
        } else if (orderId != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("formFulfilment.orderId").is(orderId));

            FormData formData = mongoOperations.findOne(query, FormData.class);
            if (formData != null) {
                data = getScreenConfig(formData.getMerchantId());
            }
        }
        return data;
    }


    private Map<String, Object> getScreenConfig(String merchantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(merchantId));
        query.fields().include("screenConfig");

        MerchantConfiguration merchantConfiguration = mongoOperations.findOne(query, MerchantConfiguration.class);
        if (merchantConfiguration != null) {
            return merchantConfiguration.getPostOrderScreenConfig();
        } else {
            return null;
        }
    }
}
