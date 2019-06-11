package com.paytm.digital.education.form.service.impl;


import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.FormFulfilment;
import com.paytm.digital.education.form.producer.KafkaProducer;
import com.paytm.digital.education.form.request.FulfilmentKafkaObject;
import com.paytm.digital.education.form.request.FulfilmentKafkaPostDataObject;
import com.paytm.digital.education.form.service.StatusCheckService;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Data
@Service
@Slf4j
public class StatusCheckServiceImpl implements StatusCheckService {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private Environment env;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Value("${app.topic.order.status.update}")
    private String topic;

    private final Set<String> terminatedStatus = new HashSet<>(Arrays.asList("success", "failure"));

    @Override
    public void updateStatusToFulfilment(String orderId) {
        try {
            // fetch order data from formData collection
            FormData formData = fetchDataFromFormDataCollection(orderId);

            //update fulfilment in case of terminated order status
            if (formData != null && terminatedStatus.contains(formData.getFormFulfilment().getPaymentStatus())) {
                log.info("Updating order status at Fulfilment: " + formData.getFormFulfilment().getPaymentStatus()
                        + " for " + orderId);
                notifyOrderStatusToFulfilment(formData);
            }
        } catch (Exception e) {
            // todo: send metrics
            log.error("StatusCheckServiceImpl :: processStatusCheck", e);
        }

    }

    private FormData fetchDataFromFormDataCollection(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("formFulfilment.orderId").is(Long.parseLong(orderId)));

        return mongoOperations.findOne(query, FormData.class);
    }

    private void notifyOrderStatusToFulfilment(FormData formData) {

        FormFulfilment formFulfilment = formData.getFormFulfilment();
        String postAction = env.getProperty("fulfilment.postaction." + formFulfilment.getPaymentStatus());
        String fulfilmentStatus = env.getProperty("fulfilment.status." + formFulfilment.getPaymentStatus());

        // construct kafka message
        FulfilmentKafkaPostDataObject fulfilmentKafkaPostDataObject = new FulfilmentKafkaPostDataObject();
        fulfilmentKafkaPostDataObject.setPostActions(postAction);
        fulfilmentKafkaPostDataObject.setStatus(fulfilmentStatus);

        // construct URL
        String url = env.getProperty("fulfilment.host.url") + '/' + formData.getMerchantId()
                + env.getProperty("fulfilment.path.update") + '/' + formData.getFormFulfilment().getFulfilmentId()
                + "?order_id=" + formData.getFormFulfilment().getOrderId();

        FulfilmentKafkaObject fulfilmentKafkaObject = new FulfilmentKafkaObject();
        fulfilmentKafkaObject.setRefId(formData.getId());
        fulfilmentKafkaObject.setUrl(url);
        fulfilmentKafkaObject.setOrderId(formData.getFormFulfilment().getOrderId());
        fulfilmentKafkaObject.setFulfilmentId(formData.getFormFulfilment().getFulfilmentId());
        fulfilmentKafkaObject.setPostData(fulfilmentKafkaPostDataObject);

        kafkaProducer.sendMessage(topic, JsonUtils.toJson(fulfilmentKafkaObject));
    }

}
