package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.model.MerchantProductConfig;
import com.paytm.digital.education.form.service.MerchantProductConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Service
public class MerchantProductConfigServiceImpl implements MerchantProductConfigService {

    private MongoOperations mongoOperations;

    private MongoTemplate mongoTemplate;

    @Override
    public MerchantProductConfig getConfig(String merchantId, String productId, List<String> keys) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(productId));
        query.addCriteria(Criteria.where("merchantId").is(merchantId));
        keys.forEach(key -> query.fields().include(key));

        return mongoOperations.findOne(query, MerchantProductConfig.class);
    }

    @Override
    public boolean saveConfig(MerchantProductConfig merchantProductConfig) {
        if (merchantProductConfig.getProductId() == null || merchantProductConfig.getMerchantId() == null) {
            return false;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(merchantProductConfig.getProductId()));
        query.addCriteria(Criteria.where("merchantId").is(merchantProductConfig.getMerchantId()));

        MerchantProductConfig foundMerchantConfig = mongoOperations.findOne(query, MerchantProductConfig.class);

        Date date = new Date();
        if (foundMerchantConfig == null) {
            foundMerchantConfig = merchantProductConfig;
            foundMerchantConfig.setCreatedDate(date);
        } else {
            // merge data
            Map<String, Object> map;
            if (foundMerchantConfig.getData() == null) {
                map = new HashMap<>();
            } else {
                map = foundMerchantConfig.getData();
            }
            map.putAll(merchantProductConfig.getData());
            foundMerchantConfig.setData(map);
        }
        foundMerchantConfig.setUpdatedDate(date);

        mongoOperations.save(foundMerchantConfig);

        return true;
    }

    @Override
    public List<MerchantProductConfig> getAllConfigs(String merchantId, Set<String> pids,
            List<String> keys) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(pids));
        query.addCriteria(Criteria.where("merchantId").is(merchantId));
        keys.forEach(key -> query.fields().include(key));

        return mongoOperations.find(query, MerchantProductConfig.class);
    }
}
