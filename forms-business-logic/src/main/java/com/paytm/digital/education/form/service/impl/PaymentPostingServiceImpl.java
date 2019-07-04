package com.paytm.digital.education.form.service.impl;


import com.mongodb.client.result.UpdateResult;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.form.constants.FblConstants;
import com.paytm.digital.education.form.dao.FormDataDao;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.FormFulfilment;
import com.paytm.digital.education.form.model.FormStatus;
import com.paytm.digital.education.form.model.MerchantProductConfig;
import com.paytm.digital.education.form.model.PaymentUserMetaData;
import com.paytm.digital.education.form.producer.KafkaProducer;
import com.paytm.digital.education.form.request.FormIoMerchantDataRequest;
import com.paytm.digital.education.form.request.FormIoMerchantRequest;
import com.paytm.digital.education.form.request.FulfilmentKafkaObject;
import com.paytm.digital.education.form.request.FulfilmentKafkaPostDataObject;
import com.paytm.digital.education.form.request.PaymentPostingItemRequest;
import com.paytm.digital.education.form.request.PaymentPostingRequest;
import com.paytm.digital.education.form.response.FormIoMerchantResponse;
import com.paytm.digital.education.form.response.FormIoMerchantResultResponse;
import com.paytm.digital.education.form.service.PaymentPostingService;
import com.paytm.digital.education.form.service.PersonaHttpClientService;
import com.paytm.digital.education.predictor.model.PredictorStats;
import com.paytm.digital.education.predictor.repository.PredictorStatsRepository;
import com.paytm.digital.education.service.S3Service;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;


/*
    Payment posting flow:
    > Insert data in formData collection
    > Fetch fulfilment id using fulfilment API
    > Update fulfilment id in formData collection
    > Hit form.io API
    > Update order status in formData collection
    > Update order status to fulfilment via kafka
*/

@Data
@Service
@Slf4j
public class PaymentPostingServiceImpl implements PaymentPostingService {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private PersonaHttpClientService personaHttpClientService;

    @Autowired
    private MerchantProductConfigServiceImpl merchantProductConfigService;

    @Autowired
    private Environment env;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FormDataDao formDataDao;

    @Autowired
    private PredictorStatsRepository predictorStatsRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AwsConfig awsConfig;

    private final Set<String> paymentStatus = new HashSet<>(Arrays.asList("success", "pending", "failure"));

    @Value("${app.topic.order.status.update}")
    private String topic;

    @Override
    public boolean processPaymentPosting(PaymentPostingRequest paymentPostingRequest) {
        try {
            // fetch item from request
            // todo: handle multiple items
            log.info("Received payment posting: {}", JsonUtils.toJson(paymentPostingRequest));
            PaymentPostingItemRequest paymentPostingItemRequest = paymentPostingRequest.getItems().get(0);

            // validate price
            Float minPrice = 0.0f;
            if (paymentPostingItemRequest.getPrice().compareTo(minPrice) < 0) {
                // todo: send metrics
                log.error("Negative price found: " + paymentPostingItemRequest.getPrice());
                return false;
            }

            // parse metadata
            final String metaData = paymentPostingItemRequest.getMetaData();
            PaymentUserMetaData metadata = null;
            try {
                metadata = JsonUtils.fromJson(metaData, PaymentUserMetaData.class);
                if (metadata == null) {
                    log.error("Error parsing metadata: " + metaData);
                }
            } catch (Exception e) {
                // todo: send metrics
                log.error("Error parsing metadata: " + metaData, e);
                return false;
            }

            String refId = metadata.getRefId();
            if (refId == null || refId.isEmpty()) {
                // todo: send metrics
                log.error("refId not found in metadata: " + metaData);
                return false;
            }

            formDataDao.updateStatus(refId, FormStatus.PG_PAYMENT_DONE);

            // fetch data and status from formData collection using refId
            FormData formData = fetchDataFromFormDataCollection(refId);

            // validate amount
            if (formData == null || formData.getCandidateDetails().getAmount() == null) {
                // todo: send metrics
                log.error("amount not found for refId: " + refId);
                return false;
            }
            if (!(paymentPostingItemRequest.getPrice().equals(formData.getCandidateDetails().getAmount()))) {
                // todo: send metrics
                log.error("amount validation failed for refId: " + refId + " " + paymentPostingItemRequest.getPrice()
                        + " " + formData.getCandidateDetails().getAmount());
                return false;
            }

            // check for txn type
            if (formData.getTransactionType() == null || formData.getTransactionType().isEmpty()) {
                // todo: send metrics
                log.error("transaction type not present for refId: " + refId);
                return false;
            }
            log.info("Current transaction type found for refId " + refId + " is " + formData.getTransactionType());

            // insert order data in formData collection
            if (!insertInFormDataCollection(refId, paymentPostingItemRequest)) {
                // todo: send metrics
                log.error("Order already exists for refId " + refId);
                return false;
            }

            // fetch fulfilment id
            Long fulfilmentId = fetchFulfilmentId(paymentPostingItemRequest, refId);
            if (fulfilmentId == null) {
                // todo: send metrics
                log.error("Fulfilment id was not created for refId " + refId);
                return false;
            }
            log.info("Fulfilment id for " + refId + " created is " + fulfilmentId.toString());

            // update fulfilment id in formData collection
            updateFulfilmentIdInFormDataCollection(refId, fulfilmentId);


            // hit form.io API
            FormIoMerchantResponse formIoMerchantResponse = notifyMerchant(paymentPostingRequest,
                    paymentPostingItemRequest, formData, refId);

            if (formIoMerchantResponse == null) {
                log.error("Response from formio API found null for refId " + refId + ", setting it as PENDING");
                formIoMerchantResponse = new FormIoMerchantResponse();
                formIoMerchantResponse.setPaymentStatus("pending");  // PENDING
            }

            // check if payment status fetched is null
            if (formIoMerchantResponse.getPaymentStatus() == null) {
                log.error("Payment status from formio API found null for refId " + refId + ", setting it as PENDING");
                formIoMerchantResponse.setPaymentStatus("pending");  // PENDING
            }

            // converting paymentStatus to lowercase
            formIoMerchantResponse.setPaymentStatus(formIoMerchantResponse.getPaymentStatus().toLowerCase());

            // check if unrecognized status is found
            if (!paymentStatus.contains(formIoMerchantResponse.getPaymentStatus())) {
                log.error("Unrecognized payment status found from formio API for refId " + refId
                        + ", setting it as PENDING");
                formIoMerchantResponse.setPaymentStatus("pending");  // PENDING
            }
            log.info("Setting payment status as " + formIoMerchantResponse.getPaymentStatus() + " for refId" + refId);

            // update order status in formData
            updateOrderStatusInFormDataCollection(refId, formIoMerchantResponse);

            // update fulfilment about the order status
            notifyOrderStatusToFulfilment(
                    refId,
                    paymentPostingItemRequest,
                    fulfilmentId,
                    formIoMerchantResponse.getPaymentStatus());

            log.info("Payment posting workflow completed for refId " + refId + " and order id "
                    + paymentPostingItemRequest.getOrderId());
        } catch (Exception e) {
            // todo: send metrics
            log.error("Error in processPaymentPosting" + paymentPostingRequest.getItems(), e);
        }
        return true;
    }

    private Long fetchFulfilmentId(PaymentPostingItemRequest paymentPostingItemRequest,
                                   String formFulfilmentCollectionId) {
        // construct URL
        String url = env.getProperty("fulfilment.host.url") + '/' + paymentPostingItemRequest.getMerchantId().toString()
                + env.getProperty("fulfilment.path.create") + '/' + paymentPostingItemRequest.getOrderId().toString();
        log.info("Hitting fulfilment create: " + url);
        // construct request body
        Map<String, Object> map = new HashMap<>();
        map.put("merchant_id", paymentPostingItemRequest.getMerchantId());
        map.put("order_id", paymentPostingItemRequest.getOrderId());
        map.put("status", Integer.parseInt(env.getProperty("fulfilment.status.authorized")));
        map.put("fulfillment_service_id", Integer.parseInt(env.getProperty("fulfilment.service.id")));
        map.put("merchant_track_id", formFulfilmentCollectionId);
        map.put("order_item_ids", new long[]{paymentPostingItemRequest.getItemId()});
        // construct header
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // hit persona
        try {
            ResponseEntity<HashMap> responseEntity = personaHttpClientService.makeHttpRequest(
                    url, HttpMethod.POST, headers, null, map, HashMap.class);
            return (Long) responseEntity.getBody().get("fulfillment_id");
        } catch (Exception e) {
            log.error("Error creating fulfillment id", e);
            return null;
        }
    }

    private Boolean insertInFormDataCollection(String id, PaymentPostingItemRequest paymentPostingItemRequest) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id)
                .orOperator(
                        Criteria.where("formFulfilment.orderId").exists(false),
                        Criteria.where("formFulfilment.orderId").is(null)
                ));

        // Creating model object
        FormFulfilment formFulfilment = new FormFulfilment();
        formFulfilment.setOrderId(paymentPostingItemRequest.getOrderId());
        formFulfilment.setItemId(paymentPostingItemRequest.getItemId());
        formFulfilment.setProductId(paymentPostingItemRequest.getProductId());
        formFulfilment.setAmount(paymentPostingItemRequest.getPrice());
        formFulfilment.setStatusCheckAttempts(0);
        formFulfilment.setPaymentStatus("pending"); // setting status as PENDING
        Date currentDate = new Date();
        formFulfilment.setCreatedDate(currentDate);
        formFulfilment.setUpdatedDate(currentDate);

        Update update = new Update();
        update.set("updatedAt", currentDate);
        update.set("formFulfilment", formFulfilment);

        // todo: why is this failing when already exist ?
        UpdateResult updateResult = mongoOperations.updateFirst(query, update, FormData.class);

        return (updateResult.getModifiedCount() > 0);
    }

    private FormIoMerchantResponse notifyMerchant(PaymentPostingRequest paymentPostingRequest,
                                                  PaymentPostingItemRequest paymentPostingItemRequest,
                                                  FormData formData, String refId) throws Exception {
        // fetch URL from merchant configuration
        String urlKey = "data." + formData.getTransactionType().toLowerCase();
        ArrayList<String> keys = new ArrayList<>();
        keys.add(urlKey);
        MerchantProductConfig merchantProductConfig = merchantProductConfigService.getConfig(paymentPostingItemRequest
                .getMerchantId().toString(), paymentPostingItemRequest.getProductId().toString(), keys);

        // parse url fetched
        String url;
        try {
            url = (String) ((Map<String, Object>) merchantProductConfig.getData()
                    .get(formData.getTransactionType().toLowerCase())).get("postingUrl");
        } catch (Exception e) {
            log.error("Error while parsing formio url from config for merchant id " + paymentPostingItemRequest
                    .getMerchantId() + " and product id " + paymentPostingItemRequest.getProductId());
            return null;
        }
        if (url == null) {
            log.error("postingUrl from config found is null for merchant id " + paymentPostingItemRequest
                    .getMerchantId() + " and product id " + paymentPostingItemRequest.getProductId());
            return null;
        }
        log.info("Merchant url fetched: " + url);

        HttpPost postRequest = new HttpPost(url);

        // set headers
        postRequest.addHeader("content-type", "application/json; charset=UTF-8");
        postRequest.addHeader("x-user-id", paymentPostingRequest.getCustomerId().toString());

        // set body
        FormIoMerchantDataRequest formIoMerchantDataRequest = new FormIoMerchantDataRequest();
        formIoMerchantDataRequest.setOrderId(paymentPostingItemRequest.getOrderId().toString());
        formIoMerchantDataRequest.setItemId(paymentPostingItemRequest.getItemId().toString());
        formIoMerchantDataRequest.setAmount(paymentPostingItemRequest.getPrice().toString());
        formIoMerchantDataRequest.setMerchantProductId(formData.getMerchantProductId());
        formIoMerchantDataRequest.setMerchantCandidateId(formData.getMerchantCandidateId());
        formIoMerchantDataRequest.setSubmit(true);
        formIoMerchantDataRequest.setRefId(refId);

        List<String> requestFormFields;
        try {
            requestFormFields = (ArrayList<String>) ((Map<String, Object>) merchantProductConfig.getData()
                    .get(formData.getTransactionType().toLowerCase())).get("requestFormFields");
        } catch (Exception e) {
            log.info("requestFormFields not found for merchant id " + paymentPostingItemRequest
                    .getMerchantId() + " and product id " + paymentPostingItemRequest.getProductId());
            requestFormFields = null;
        }
        if (requestFormFields != null && requestFormFields.size() > 0) {
            FormData requestFormData = fetchCustomFormData(refId, requestFormFields);
            formIoMerchantDataRequest.setCandidateDetails(requestFormData.getCandidateDetails());
            formIoMerchantDataRequest.setAdditionalData(requestFormData.getAdditionalData());
        }

        FormIoMerchantRequest formIoMerchantRequest = new FormIoMerchantRequest();
        formIoMerchantRequest.setData(formIoMerchantDataRequest);
        formIoMerchantRequest.setState("submitted");

        postRequest.setEntity(new StringEntity(JsonUtils.toJson(formIoMerchantRequest)));

        //log.info("Merchant API Request for refId {} : {}", refId, JsonUtils.toJson(postRequest));
        // execute post call
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(postRequest);

        // parse response
        HttpEntity httpEntity = response.getEntity();
        String apiOutput = EntityUtils.toString(httpEntity);
        log.info("Merchant API Output for refId " + refId + ": " + apiOutput);
        FormIoMerchantResultResponse formIoMerchantResultResponse = JsonUtils
                .fromJson(apiOutput, FormIoMerchantResultResponse.class);

        // close connection
        httpClient.getConnectionManager().shutdown();

        if (formIoMerchantResultResponse != null) {
            if (!CollectionUtils.isEmpty(merchantProductConfig.getData()) && merchantProductConfig
                    .getData().containsKey(FblConstants.SERVICE) && merchantProductConfig.getData()
                    .get(FblConstants.SERVICE)
                    .equals(FblConstants.PREDICTOR)) {
                updatePredictorStats(formData, merchantProductConfig);
                uploadAndUpdateS3Link(refId, formIoMerchantResultResponse.getResult());
            }
            return formIoMerchantResultResponse.getResult();
        }
        return null;
    }

    private void updatePredictorStats(FormData formData,
            MerchantProductConfig merchantProductConfig) {
        PredictorStats predictorStats = predictorStatsRepository
                .findByCustomerIdAndMerchantProductId(formData.getCustomerId(),
                        formData.getMerchantProductId());
        if (Objects.isNull(predictorStats)) {
            predictorStats = new PredictorStats();
            predictorStats.setMerchantId(formData.getMerchantId());
            predictorStats.setMerchantProductId(formData.getMerchantProductId());
            predictorStats.setCustomerId(formData.getCustomerId());
            predictorStats.setUseCount(1);
            predictorStats.setCreatedAt(new Date());
        } else if (!CollectionUtils.isEmpty(merchantProductConfig.getData())
                && merchantProductConfig.getData().containsKey(FblConstants.MAX_USAGE)
                && merchantProductConfig.getData().get(FblConstants.MAX_USAGE) == predictorStats
                        .getUseCount()) {
            predictorStats.setUseCount(1);
        } else {
            predictorStats.setUseCount(predictorStats.getUseCount() + 1);
        }
        predictorStats.setUpdatedAt(new Date());
        predictorStatsRepository.save(predictorStats);
    }

    private void uploadAndUpdateS3Link(String refId,
            FormIoMerchantResponse formIoMerchantResponse) {
        if (formIoMerchantResponse.getCandidateDetails().containsKey("predictorUrl")) {
            String urlStr =
                    (String) formIoMerchantResponse.getCandidateDetails().get("predictorUrl");
            try {
                URL url = new URL(urlStr);
                InputStream stream = url.openStream();
                String s3Url = s3Service
                        .uploadFile(stream, refId, refId, FblConstants.PREDICTOR_S3_RELATIVE_PATH,
                                awsConfig.getS3ExploreBucketName());
                if (StringUtils.isNotBlank(s3Url)) {
                    formIoMerchantResponse.getCandidateDetails().put("predictorUrl", s3Url);
                }
            } catch (MalformedURLException e) {
                log.error("Url building malformed for url string :{}", urlStr);
            } catch (IOException e) {
                log.error("IO Exception while downloading file for url :{}", urlStr);
            }
        }
    }


    private void updateFulfilmentIdInFormDataCollection(String id, Long fulfilmentId) {
        Update update = new Update();
        update.set("formFulfilment.fulfilmentId", fulfilmentId);
        Date currentDate = new Date();
        update.set("updatedAt", currentDate);
        update.set("formFulfilment.updatedDate", currentDate);

        mongoOperations.updateFirst(new Query(Criteria.where("_id").is(id)), update, FormData.class);
    }

    private FormData fetchDataFromFormDataCollection(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.fields().include("candidateDetails.amount");
        query.fields().include("transactionType");
        query.fields().include("merchantProductId");
        query.fields().include("merchantCandidateId");

        return mongoOperations.findOne(query, FormData.class);
    }

    private FormData fetchCustomFormData(String id, List<String> inclusionList) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        for (String key: inclusionList) {
            query.fields().include(key);
        }

        return mongoOperations.findOne(query, FormData.class);
    }

    private void updateOrderStatusInFormDataCollection(String id, FormIoMerchantResponse formIoMerchantResponse) {
        Update update = new Update();
        update.set("formFulfilment.paymentStatus", formIoMerchantResponse.getPaymentStatus());
        if (formIoMerchantResponse.getMerchantTransactionId() != null) {
            update.set("formFulfilment.merchantTransactionId", formIoMerchantResponse.getMerchantTransactionId());
        }
        Date currentDate = new Date();
        update.set("formFulfilment.updatedDate", currentDate);
        update.set("updatedAt", currentDate);
        update.set("status", env.getProperty("formio.status." + formIoMerchantResponse.getPaymentStatus()));
        // update response in candidateDetails
        if (formIoMerchantResponse.getCandidateDetails() != null) {
            for (Map.Entry<String, Object> entry : formIoMerchantResponse.getCandidateDetails().entrySet()) {
                update.set("candidateDetails." + entry.getKey(), entry.getValue());
            }
        }

        mongoOperations.updateFirst(new Query(Criteria.where("_id").is(id)), update, FormData.class);
    }

    private void notifyOrderStatusToFulfilment(String refId,
                                               PaymentPostingItemRequest paymentPostingItemRequest,
                                               Long fulfilmentId, String responsecode) {
        // construct URL
        String url = env.getProperty("fulfilment.host.url") + '/' + paymentPostingItemRequest.getMerchantId().toString()
                + env.getProperty("fulfilment.path.update") + '/' + fulfilmentId.toString() + "?order_id="
                + paymentPostingItemRequest.getOrderId().toString();
        log.info("Created fulfilment update url: " + url);
        // construct kafka message
        FulfilmentKafkaPostDataObject fulfilmentKafkaPostDataObject = new FulfilmentKafkaPostDataObject();
        fulfilmentKafkaPostDataObject.setPostActions(env.getProperty("fulfilment.postaction." + responsecode));
        fulfilmentKafkaPostDataObject.setStatus(env.getProperty("fulfilment.status." + responsecode));

        FulfilmentKafkaObject fulfilmentKafkaObject = new FulfilmentKafkaObject();
        fulfilmentKafkaObject.setRefId(refId);
        fulfilmentKafkaObject.setUrl(url);
        fulfilmentKafkaObject.setOrderId(paymentPostingItemRequest.getOrderId());
        fulfilmentKafkaObject.setFulfilmentId(fulfilmentId);
        fulfilmentKafkaObject.setPostData(fulfilmentKafkaPostDataObject);

        kafkaProducer.sendMessage(topic, JsonUtils.toJson(fulfilmentKafkaObject));
    }
}
