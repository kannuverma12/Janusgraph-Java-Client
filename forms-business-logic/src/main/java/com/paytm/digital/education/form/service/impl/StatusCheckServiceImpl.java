package com.paytm.digital.education.form.service.impl;


import com.paytm.digital.education.form.model.FormData;
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

    private final String[] terminatedStatus = new String[]{"00", "08"};

    @Override
    public void processStatusCheck(String orderId) {
        try {
            // fetch order data from formData collection
            FormData formData = fetchDataFromFormDataCollection(orderId);

            //update fulfilment in case of terminated order status
            if (formData != null && Arrays.asList(terminatedStatus)
                    .contains(formData.getFormFulfilment().getPaymentStatus())) {
                notifyOrderStatusToFulfilment(formData);
            }
        } catch (Exception e) {
            log.error("StatusCheckServiceImpl :: processStatusCheck", e);
        }

    }

    private FormData fetchDataFromFormDataCollection(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("formFulfilment.orderId").is(Long.parseLong(orderId)));

        return mongoOperations.findOne(query, FormData.class);
    }

    private void notifyOrderStatusToFulfilment(FormData formData) {
        // construct URL
        String url = env.getProperty("fulfilment.host.url") + '/' + formData.getMerchantId()
                + env.getProperty("fulfilment.path.update") + '/' + formData.getFormFulfilment().getFulfilmentId()
                + "?order_id=" + formData.getFormFulfilment().getOrderId();

        // construct kafka message
        FulfilmentKafkaPostDataObject fulfilmentKafkaPostDataObject = new FulfilmentKafkaPostDataObject();
        fulfilmentKafkaPostDataObject.setPostActions(env.getProperty("fulfilment.postaction."
                + formData.getFormFulfilment().getPaymentStatus()));
        fulfilmentKafkaPostDataObject.setStatus(env.getProperty("fulfilment.status."
                + formData.getFormFulfilment().getPaymentStatus()));

        FulfilmentKafkaObject fulfilmentKafkaObject = new FulfilmentKafkaObject();
        fulfilmentKafkaObject.setUrl(url);
        fulfilmentKafkaObject.setOrderId(formData.getFormFulfilment().getOrderId());
        fulfilmentKafkaObject.setFulfilmentId(formData.getFormFulfilment().getFulfilmentId());
        fulfilmentKafkaObject.setPostData(fulfilmentKafkaPostDataObject);

        kafkaProducer.sendMessage(topic, JsonUtils.toJson(fulfilmentKafkaObject));
    }

}
