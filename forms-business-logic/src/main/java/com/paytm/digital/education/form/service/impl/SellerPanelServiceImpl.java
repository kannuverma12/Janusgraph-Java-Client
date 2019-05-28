package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.model.DownloadOrder;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.ResponseData;
import com.paytm.digital.education.form.producer.KafkaProducer;
import com.paytm.digital.education.form.service.SellerPanelService;
import com.paytm.digital.education.utility.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SellerPanelServiceImpl implements SellerPanelService {

    private final MongoOperations mongoOperations;
    private final KafkaProducer kafkaProducer;
    private final String orderFileCenterTopicName;

    public SellerPanelServiceImpl(
            MongoOperations mongoOperations,
            KafkaProducer kafkaProducer,
            @Value("${app.topic.order.filecenter}") String topicName) {
        this.mongoOperations = mongoOperations;
        this.kafkaProducer = kafkaProducer;
        this.orderFileCenterTopicName = topicName;
    }


    @Override
    public List<FormData> getInfoOnOrderIds(String merchantId, List<Long> orderIds, Date startDate, Date endDate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("formFulfilment.orderId").in(orderIds)
                .and("merchantId").is(merchantId));
        if (startDate != null) {
            query.addCriteria(Criteria.where("formFulfilment.createdDate")
                    .gte(getStartOfDay(startDate)));
        }

        if (endDate != null) {
            query.addCriteria(Criteria.where("formFulfilment.createdDate").lte(getEndOfDay(endDate)));
        }

        return mongoOperations.find(query, FormData.class);
    }

    @Override
    public ResponseData<FormData> getInfoOnDate(
            String merchantId, Date startDate, Date endDate, int offset, int limit) {

        Query query = new Query();
        Criteria criteria = Criteria.where("formFulfilment.createdDate")
                .gte(getStartOfDay(startDate)).lte(getEndOfDay(endDate)).and("merchantId").is(merchantId);
        query.addCriteria(criteria);
        long count = mongoOperations.count(query, "formData");

        query.skip(limit * offset);
        query.limit(limit);

        List<FormData> responses = mongoOperations.find(query, FormData.class);

        return new ResponseData<FormData>(count, responses);
    }

    @Override
    public void submitDownloadOrderRequest(DownloadOrder downloadOrder) {
        kafkaProducer.sendMessage(orderFileCenterTopicName, JsonUtils.toJson(downloadOrder));
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
