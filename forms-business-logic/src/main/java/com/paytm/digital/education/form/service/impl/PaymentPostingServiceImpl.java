package com.paytm.digital.education.form.service.impl;


import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.form.constants.FblConstants;
import com.paytm.digital.education.form.dao.FormDataDao;
import com.paytm.digital.education.form.dao.PaymentPostingErrorDao;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.FormFulfilment;
import com.paytm.digital.education.form.model.FormStatus;
import com.paytm.digital.education.form.model.MerchantProductConfig;
import com.paytm.digital.education.form.model.PaymentState;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


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
    private PaymentPostingErrorDao paymentPostingErrorDao;

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
            log.info("Received payment posting Request: {}", paymentPostingRequest);
            PaymentPostingItemRequest paymentPostingItemRequest =
                    paymentPostingRequest.getItems().get(0);

            Float minPrice = 0.0f;
            if (paymentPostingItemRequest.getPrice().compareTo(minPrice) < 0) {
                paymentPostingErrorDao
                        .upsertRecord(null, "Negative price found", paymentPostingRequest.getId(),
                                paymentPostingItemRequest);
                log.error("Negative price found: " + paymentPostingItemRequest.getPrice());
                nackOrder(paymentPostingItemRequest, null);
                return true;
            }
            String refId = getRefId(paymentPostingRequest.getId(), paymentPostingItemRequest);
            if (StringUtils.isBlank(refId)) {
                paymentPostingErrorDao
                        .upsertRecord(null, "RefId not found in meta: " + paymentPostingItemRequest
                                        .getMetaData(), paymentPostingRequest.getId(),
                                paymentPostingItemRequest);
                log.error("refId not found in metadata: " + paymentPostingItemRequest.getMetaData());
                nackOrder(paymentPostingItemRequest, null);
                return true;
            }
            FormData formData = fetchDataFromFormDataCollection(refId);
            if (formData == null) {
                paymentPostingErrorDao.upsertRecord(refId, "Form data not found in DB",
                        paymentPostingRequest.getId(), paymentPostingItemRequest);
                log.error("Form data not found for refId: " + refId);
                nackOrder(paymentPostingItemRequest, refId);
                return true;
            }
            if (formData.getCandidateDetails().getAmount() == null) {
                paymentPostingErrorDao.upsertRecord(refId, "Amount not found in DB",
                        paymentPostingRequest.getId(), paymentPostingItemRequest);
                log.error("Amount not found for refId: " + refId);
                nackOrder(paymentPostingItemRequest, refId);
                return true;
            }

            if (formData.getFormFulfilment() != null) {
                if (formData.getFormFulfilment().getItemId() != null && !formData.getFormFulfilment()
                        .getItemId().equals(paymentPostingItemRequest.getItemId())) {
                    paymentPostingErrorDao.upsertRecord(refId, "Invalid item found in request",
                            paymentPostingRequest.getId(), paymentPostingItemRequest);
                    log.error("Invalid item found in request for {} {} {}", refId,
                            formData.getFormFulfilment().getItemId(),
                            paymentPostingItemRequest.getItemId());
                    nackOrder(paymentPostingItemRequest, refId);
                    return true;
                }

                if (formData.getFormFulfilment().getPaymentStatus() != null && !formData.getFormFulfilment()
                        .getPaymentStatus().equalsIgnoreCase(FblConstants.PENDING_STRING)) {
                    paymentPostingErrorDao.upsertRecord(refId, "Payment already done",
                            paymentPostingRequest.getId(), paymentPostingItemRequest);
                    log.error("Payment already done for {} {}", refId,
                            formData.getFormFulfilment().getPaymentStatus());
                    return true;
                }
            }

            if (!(paymentPostingItemRequest.getPrice()
                    .equals(formData.getCandidateDetails().getAmount()))) {
                paymentPostingErrorDao.upsertRecord(refId,
                        "Amount validation failed. Request amt: " + paymentPostingItemRequest
                                .getPrice().toString() + " DB amt: " + formData
                                .getCandidateDetails().getAmount().toString(),
                        paymentPostingRequest.getId(), paymentPostingItemRequest);
                log.error("Amount validation failed for refId: {} {} {}", refId, paymentPostingItemRequest.getPrice(),
                        formData.getCandidateDetails().getAmount());
                nackOrder(paymentPostingItemRequest, refId);
                return true;
            }

            if (formData.getTransactionType() == null || formData.getTransactionType().isEmpty()) {
                paymentPostingErrorDao.upsertRecord(refId, "Transaction type not present",
                        paymentPostingRequest.getId(), paymentPostingItemRequest);
                log.error("Transaction type not present for refId: " + refId);
                nackOrder(paymentPostingItemRequest, refId);
                return true;
            }
            log.debug("Current transaction type found for refId " + refId + " is " + formData
                    .getTransactionType());

            if (formData.getFormFulfilment() == null || formData.getFormFulfilment().getOrderId() == null) {
                insertInFormDataCollection(refId, paymentPostingItemRequest,
                        FormStatus.PG_PAYMENT_DONE, PaymentState.ORDER_ID_UPDATED);
            } else {
                formDataDao.updateStatus(refId, FormStatus.PG_PAYMENT_DONE);
            }

            Long fulfilmentId = null;
            if (formData.getFormFulfilment() != null) {
                fulfilmentId = formData.getFormFulfilment().getFulfilmentId();
            }
            if (fulfilmentId == null) {
                fulfilmentId = createFulfilmentId(refId, paymentPostingItemRequest);
                if (fulfilmentId == null) {
                    paymentPostingErrorDao.upsertRecord(refId, "Error creating fulfillment id",
                            paymentPostingRequest.getId(), paymentPostingItemRequest);
                    log.error("Error creating fulfillment id");
                    return false;
                }
                updateFulfilmentIdInFormDataCollection(refId, fulfilmentId, PaymentState.FF_ID_UPDATED);
            }

            FormIoMerchantResponse formIoMerchantResponse = notifyMerchant(paymentPostingRequest,
                    paymentPostingItemRequest, formData, refId);
            if (formIoMerchantResponse == null) {
                log.error("Response from formio API found null for refId " + refId + ", setting it as PENDING");
                formIoMerchantResponse = new FormIoMerchantResponse();
                formIoMerchantResponse.setPaymentStatus("pending");
            }
            if (formIoMerchantResponse.getPaymentStatus() == null) {
                log.error("Payment status from formio API found null for refId " + refId + ", setting it as PENDING");
                formIoMerchantResponse.setPaymentStatus("pending");
            }
            // converting paymentStatus to lowercase
            formIoMerchantResponse.setPaymentStatusToLowerCase();

            if (!paymentStatus.contains(formIoMerchantResponse.getPaymentStatus())) {
                log.error("Unrecognized payment status found from formio API for refId " + refId
                        + ", setting it as PENDING");
                formIoMerchantResponse.setPaymentStatus("pending");
            }
            log.info("Setting payment status as " + formIoMerchantResponse.getPaymentStatus() + " for refId" + refId);

            PaymentState paymentState = getPaymentState(formIoMerchantResponse.getPaymentStatus());
            FormStatus formStatus = getFormStatus(formIoMerchantResponse.getPaymentStatus());
            updateOrderStatusInFormDataCollection(refId, formIoMerchantResponse, formStatus, paymentState);

            if (paymentState.equals(PaymentState.PENDING)) {
                return false;
            }
            notifyOrderStatusToFulfilment(
                    refId,
                    paymentPostingItemRequest,
                    fulfilmentId,
                    formIoMerchantResponse.getPaymentStatus());

            log.info("Payment posting workflow completed for refId " + refId + " and order id "
                    + paymentPostingItemRequest.getOrderId());
        } catch (Exception e) {
            log.error("Error in processPaymentPosting {} {}", paymentPostingRequest, e);
            return false;
        }
        return true;
    }

    private PaymentState getPaymentState(String status) {
        if (status.equalsIgnoreCase(FblConstants.SUCCESS_STRING)) {
            return PaymentState.SUCCESS;
        } else if (status.equalsIgnoreCase(FblConstants.FAILURE_STRING)) {
            return PaymentState.FAILURE;
        } else {
            return PaymentState.PENDING;
        }
    }

    private FormStatus getFormStatus(String status) {
        if (status.equalsIgnoreCase(FblConstants.SUCCESS_STRING)) {
            return FormStatus.SUCCESS;
        } else if (status.equalsIgnoreCase(FblConstants.FAILURE_STRING)) {
            return FormStatus.FAILURE;
        } else {
            return FormStatus.PENDING;
        }
    }

    private String getRefId(String requestId, PaymentPostingItemRequest paymentPostingItemRequest) {
        PaymentUserMetaData metadata = null;
        try {
            metadata = JsonUtils.fromJson(paymentPostingItemRequest.getMetaData(), PaymentUserMetaData.class);
            if (metadata == null) {
                paymentPostingErrorDao.upsertRecord(null, "Error parsing metadata: "
                        + paymentPostingItemRequest.getMetaData(), requestId, paymentPostingItemRequest);
                log.error("Error parsing metadata: " + paymentPostingItemRequest.getMetaData());
                return null;
            }
        } catch (Exception e) {
            paymentPostingErrorDao.upsertRecord(null, "Error parsing metadata: "
                    + paymentPostingItemRequest.getMetaData() + e, requestId, paymentPostingItemRequest);
            log.error("Error parsing metadata:", e);
            return null;
        }
        return metadata.getRefId();
    }

    private Long createFulfilmentId(String formFulfilmentCollectionId,
                                    PaymentPostingItemRequest paymentPostingItemRequest) {
        // construct URL
        String url = env.getProperty("fulfilment.host.url") + '/' + paymentPostingItemRequest.getMerchantId()
                + env.getProperty("fulfilment.path.create") + '/' + paymentPostingItemRequest.getOrderId();
        log.debug("Hitting fulfilment create: " + url);
        // construct request body
        Map<String, Object> map = new HashMap<>();
        map.put("merchant_id", paymentPostingItemRequest.getMerchantId());
        map.put("order_id", paymentPostingItemRequest.getOrderId());
        map.put("status", Integer.parseInt(env.getProperty("fulfilment.status.authorized")));
        map.put("fulfillment_service_id",
                Integer.parseInt(env.getProperty("fulfilment.service.id")));
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
            paymentPostingErrorDao
                    .upsertRecord(formFulfilmentCollectionId, "Error creating fulfillment id" + e,
                            null, paymentPostingItemRequest);
            log.error("Error creating fulfillment id", e);
            return null;
        }
    }

    private void nackOrder(PaymentPostingItemRequest paymentPostingItemRequest, String refId) {
        String url = env.getProperty("fulfilment.host.url") + '/' + paymentPostingItemRequest.getMerchantId()
                + env.getProperty("fulfilment.path.ack") + '/' + paymentPostingItemRequest.getOrderId();
        log.info("Hitting fulfilment nack: " + url);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> map = new HashMap<>();
        map.put("status", 0);

        try {
            personaHttpClientService.makeHttpRequest(url, HttpMethod.POST, headers, null, map, Object.class);
        } catch (Exception e) {
            paymentPostingErrorDao.upsertRecord(refId, "Error while cancelling order in fulfilment" + e,
                    paymentPostingItemRequest.getOrderId().toString(), paymentPostingItemRequest);
            log.error("Error while cancelling order in fulfilment", e);
        }
    }

    private void insertInFormDataCollection(String id, PaymentPostingItemRequest paymentPostingItemRequest,
                                            FormStatus status, PaymentState paymentState) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        // Creating model object
        FormFulfilment formFulfilment = new FormFulfilment();
        formFulfilment.setOrderId(paymentPostingItemRequest.getOrderId());
        formFulfilment.setItemId(paymentPostingItemRequest.getItemId());
        formFulfilment.setProductId(paymentPostingItemRequest.getProductId());
        formFulfilment.setAmount(paymentPostingItemRequest.getPrice());
        formFulfilment.setPaymentState(paymentState);
        formFulfilment.setStatusCheckAttempts(0);
        formFulfilment.setPaymentStatus("pending"); // setting status as PENDING
        Date currentDate = new Date();
        formFulfilment.setCreatedDate(currentDate);
        formFulfilment.setUpdatedDate(currentDate);

        Update update = new Update();
        update.set("status", status);
        update.set("updatedAt", currentDate);
        update.set("formFulfilment", formFulfilment);
        mongoOperations.updateFirst(query, update, FormData.class);
    }

    private FormIoMerchantResponse notifyMerchant(PaymentPostingRequest paymentPostingRequest,
                                                  PaymentPostingItemRequest paymentPostingItemRequest,
                                                  FormData formData, String refId) throws Exception {
        // fetch URL from merchant configuration
        String urlKey = "data." + formData.getTransactionType().toLowerCase();
        String service = "data." + FblConstants.SERVICE;
        String maxUsage = "data." + FblConstants.MAX_USAGE;
        ArrayList<String> keys = new ArrayList<>();
        keys.add(urlKey);
        keys.add(maxUsage);
        keys.add(service);
        MerchantProductConfig merchantProductConfig = merchantProductConfigService.getConfig(paymentPostingItemRequest
                .getMerchantId().toString(), paymentPostingItemRequest.getProductId().toString(), keys);

        // parse url fetched
        String url;
        try {
            url = (String) ((Map<String, Object>) merchantProductConfig.getData()
                    .get(formData.getTransactionType().toLowerCase())).get("postingUrl");
        } catch (Exception e) {
            log.error("Error while parsing formio url from config for merchant id "
                    + paymentPostingItemRequest
                    .getMerchantId() + " and product id " + paymentPostingItemRequest
                    .getProductId());
            return null;
        }
        if (url == null) {
            log.error("postingUrl from config found is null for merchant id "
                    + paymentPostingItemRequest
                    .getMerchantId() + " and product id " + paymentPostingItemRequest
                    .getProductId());
            return null;
        }
        log.debug("Merchant url fetched: " + url);

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
            requestFormFields =
                    (ArrayList<String>) ((Map<String, Object>) merchantProductConfig.getData()
                            .get(formData.getTransactionType().toLowerCase()))
                            .get("requestFormFields");
        } catch (Exception e) {
            log.debug("requestFormFields not found for merchant id " + paymentPostingItemRequest
                    .getMerchantId() + " and product id " + paymentPostingItemRequest
                    .getProductId());
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

        log.debug("Merchant API Request for refId {} : {}", refId,
                JsonUtils.toJson(formIoMerchantRequest));
        // execute post call
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String apiOutput = null;
        try {
            HttpResponse response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();
            apiOutput = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            log.error("Error while notifying merchant", e);
        }
        httpClient.getConnectionManager().shutdown();
        log.info("Merchant API Output for refId " + refId + ": " + apiOutput);
        log.info("Form Data : {} ", formData);
        log.info("Merchant Product Config : {}", merchantProductConfig);
        FormIoMerchantResultResponse formIoMerchantResultResponse = JsonUtils
                .fromJson(apiOutput, FormIoMerchantResultResponse.class);
        log.info("Form io merchant result response : {}", formIoMerchantResultResponse);
        if (formIoMerchantResultResponse != null) {
            if (!CollectionUtils.isEmpty(merchantProductConfig.getData()) && merchantProductConfig
                    .getData().containsKey(FblConstants.SERVICE) && merchantProductConfig.getData()
                    .get(FblConstants.SERVICE).toString()
                    .equalsIgnoreCase(FblConstants.PREDICTOR)) {

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
                .findByCustomerIdAndMerchantProductIdAndMerchantId(formData.getCustomerId(),
                        formData.getMerchantProductId(), formData.getMerchantId());
        if (Objects.isNull(predictorStats)) {
            predictorStats = new PredictorStats();
            predictorStats.setMerchantId(formData.getMerchantId());
            predictorStats.setMerchantProductId(formData.getMerchantProductId());
            predictorStats.setCustomerId(formData.getCustomerId());
            predictorStats.setUseCount(1);
            predictorStats.setCreatedAt(new Date());
        } else if (!CollectionUtils.isEmpty(merchantProductConfig.getData())
                && merchantProductConfig.getData().containsKey(FblConstants.MAX_USAGE)
                && (Integer.parseInt(
                merchantProductConfig.getData().get(FblConstants.MAX_USAGE).toString())
                == predictorStats
                .getUseCount().intValue())) {
            predictorStats.setUseCount(1);
        } else {
            predictorStats.setUseCount(predictorStats.getUseCount() + 1);
        }
        predictorStats.setUpdatedAt(new Date());
        log.info("Inserting predictor stats {}", predictorStats);
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
                String s3RelativeUrl = s3Service
                        .uploadFile(stream, refId + ".pdf", refId,
                                FblConstants.PREDICTOR_S3_RELATIVE_PATH,
                                awsConfig.getS3ExploreBucketName());
                log.info("S3 relative url: {}", s3RelativeUrl);
                if (StringUtils.isNotBlank(s3RelativeUrl)) {
                    formIoMerchantResponse.getCandidateDetails()
                            .put("predictorUrl",
                                    AwsConfig.getMediaBaseUrl()
                                            + "education/explore/college/images/"
                                            + s3RelativeUrl);
                }
            } catch (MalformedURLException e) {
                log.error("Url building malformed for url string :{}", urlStr);
            } catch (IOException e) {
                log.error("IO Exception while downloading file for url :{}", urlStr);
            }
        }
    }


    private void updateFulfilmentIdInFormDataCollection(String id, Long fulfilmentId, PaymentState state) {
        Update update = new Update();
        update.set("formFulfilment.fulfilmentId", fulfilmentId);
        update.set("formFulfilment.paymentState", state);
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
        query.fields().include("status");
        query.fields().include("formFulfilment");
        query.fields().include("merchantProductId");
        query.fields().include("merchantCandidateId");
        query.fields().include("customerId");
        query.fields().include("merchantId");

        return mongoOperations.findOne(query, FormData.class);
    }

    private FormData fetchCustomFormData(String id, List<String> inclusionList) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        for (String key : inclusionList) {
            query.fields().include(key);
        }

        return mongoOperations.findOne(query, FormData.class);
    }

    private void updateOrderStatusInFormDataCollection(String id, FormIoMerchantResponse formIoMerchantResponse,
                                                       FormStatus status, PaymentState paymentState) {
        Update update = new Update();
        update.set("formFulfilment.paymentStatus", formIoMerchantResponse.getPaymentStatus());
        update.set("formFulfilment.paymentState", paymentState);
        update.set("status", status);
        if (formIoMerchantResponse.getMerchantTransactionId() != null) {
            update.set("formFulfilment.merchantTransactionId",
                    formIoMerchantResponse.getMerchantTransactionId());
        }
        if (formIoMerchantResponse.getMerchantCandidateId() != null) {
            update.set("merchantCandidateId", formIoMerchantResponse.getMerchantCandidateId());
        }
        Date currentDate = new Date();
        update.set("formFulfilment.updatedDate", currentDate);
        update.set("updatedAt", currentDate);
        // update response in candidateDetails
        if (formIoMerchantResponse.getCandidateDetails() != null) {
            for (Map.Entry<String, Object> entry : formIoMerchantResponse.getCandidateDetails()
                    .entrySet()) {
                update.set("candidateDetails." + entry.getKey(), entry.getValue());
            }
        }
        mongoOperations.updateFirst(new Query(Criteria.where("_id").is(id)), update, FormData.class);
    }

    private void notifyOrderStatusToFulfilment(String refId,
                                               PaymentPostingItemRequest paymentPostingItemRequest,
                                               Long fulfilmentId, String responsecode) {
        // construct URL
        String url = env.getProperty("fulfilment.host.url") + '/' + paymentPostingItemRequest.getMerchantId()
                + env.getProperty("fulfilment.path.update") + '/' + fulfilmentId + "?order_id="
                + paymentPostingItemRequest.getOrderId();
        log.debug("Created fulfilment update url: " + url);
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
