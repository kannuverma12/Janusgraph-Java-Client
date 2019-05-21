package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.ResponseData;
import com.paytm.digital.education.form.service.SellerPanelService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class SellerPanelServiceImpl implements SellerPanelService {

    private MongoOperations mongoOperations;

    @Override
    public List<FormData> getInfoOnOrderIds(String merchantId, List<String> orderIds) {

        Query query = new Query();
        query.addCriteria(Criteria.where("formFulfilment.orderId").in(orderIds)
                .and("merchantId").is(merchantId));

        return mongoOperations.find(query, FormData.class);
    }

    @Override
    public ResponseData<FormData> getInfoOnDate(String merchantId, Date startDate, Date endDate,
                                                int offset, int limit) {

        Query query = new Query();
        Criteria criteria = Criteria.where("createdAt")
                .gte(startDate).lte(endDate).and("merchantId").is(merchantId);
        query.addCriteria(criteria);
        long count = mongoOperations.count(query, "formData");

        query.skip(limit * offset);
        query.limit(limit);

        List<FormData> responses = mongoOperations.find(query, FormData.class);

        return new ResponseData<FormData>(count, responses);
    }
}
