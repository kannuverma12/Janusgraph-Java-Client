package com.paytm.digital.education.form.dao;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.FormStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class FormDataDao {

    private MongoOperations mongoOperations;

    public void updateStatus(String id, FormStatus formStatus) {
        Update update = new Update();
        Date currentDate = new Date();
        update.set("formFulfilment.updatedDate", currentDate);
        update.set("updatedAt", currentDate);
        update.set("status", formStatus.toString());

        mongoOperations.updateFirst(new Query(Criteria.where("_id").is(id)), update, FormData.class);
    }

    public void updateFulfilmentStatus(String id, Integer status) {
        Update update = new Update();
        Date currentDate = new Date();
        update.set("formFulfilment.updatedDate", currentDate);
        update.set("updatedAt", currentDate);
        update.set("formFulfilment.fulfilmentStatus", status);

        mongoOperations.updateFirst(new Query(Criteria.where("_id").is(id)), update, FormData.class);
    }


}
