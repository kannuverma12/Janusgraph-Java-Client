package com.paytm.digital.education.form.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.MerchantConfiguration;
import com.paytm.digital.education.form.response.PostOrderScreenConfigResponse;
import com.paytm.digital.education.form.service.MerchantConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Service
@Slf4j
public class MerchantConfigServiceImpl implements MerchantConfigService {

    private MongoOperations mongoOperations;
    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;

    @Override
    public MerchantConfiguration getMerchantById(String merchantId, ArrayList<String> keys) {
        MerchantConfiguration merchantConfiguration;

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
            if (merchantConfiguration.getData() != null) {
                map = foundMerchantConfiguration.getData() != null
                        ? foundMerchantConfiguration.getData() : new HashMap<>();
                map.putAll(merchantConfiguration.getData());
                foundMerchantConfiguration.setData(map);
            }

            if (merchantConfiguration.getPostOrderScreenConfig() != null) {
                map = foundMerchantConfiguration.getPostOrderScreenConfig() != null
                        ? foundMerchantConfiguration.getPostOrderScreenConfig() : new HashMap<>();
                map.putAll(merchantConfiguration.getPostOrderScreenConfig());
                foundMerchantConfiguration.setPostOrderScreenConfig(map);
            }

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
            log.debug("Data found for merchant id {} is {}", merchantId, data);
            if (data == null) {
                log.error("No data found for provided merchant id = {}", merchantId);
                data = getScreenConfigByOrderId(orderId);
            }
        } else if (orderId != null) {
            data = getScreenConfigByOrderId(orderId);
        }
        return data;
    }

    @Override
    public ResponseEntity<Object> getResponseForPostOrderScreenConfig(
            Map<String, Object> data, Long orderId, String merchantId) {

        Map<String, Object> responseData = new HashMap<>();

        if (orderId != null) {
            Query query = new Query(Criteria.where("formFulfilment.orderId").is(orderId));
            query.fields().include("merchantCandidateId");

            FormData formData = mongoOperations.findOne(query, FormData.class);

            String registrationLabel = (String) data.get("registration_label");

            if (registrationLabel != null) {
                responseData.put("registration_label", registrationLabel);
                registrationLabel += " "; // adding space for appending registration id
            } else {
                registrationLabel = "";
            }

            if (formData != null && formData.getMerchantCandidateId() != null) {
                responseData.put("registration_id", registrationLabel + formData.getMerchantCandidateId());
            }
        }

        if (data.containsKey("fill_form_id")) {
            responseData.put("fill_form_id", data.get("fill_form_id"));
        }

        String formDownloadLink = (String) data.get("form_download_link");
        if (formDownloadLink != null) {
            formDownloadLink += "?order_id=" + orderId + "&type=form";
            responseData.put("form_download_link", formDownloadLink);
        }

        String invoiceDownloadLink = (String) data.get("invoice_download_link");
        if (invoiceDownloadLink != null) {
            invoiceDownloadLink += "?order_id=" + orderId + "&type=invoice";
            responseData.put("invoice_download_link", invoiceDownloadLink);
        }

        PostOrderScreenConfigResponse postOrderScreenConfigResponse
                = new PostOrderScreenConfigResponse(200, responseData);

        log.info("Response to be sent for order id = {} and merchant id = {} is -> {}",
                orderId, merchantId, postOrderScreenConfigResponse);

        return new ResponseEntity<>(postOrderScreenConfigResponse, HttpStatus.OK);
    }

    private Map<String, Object> getScreenConfigByOrderId(Long orderId) {
        Map<String, Object> data = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("formFulfilment.orderId").is(orderId));

        FormData formData = mongoOperations.findOne(query, FormData.class);
        if (formData != null) {
            data = getScreenConfig(formData.getMerchantId());
        }

        log.debug("Data found for order id = {} is {}", orderId, data);
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
