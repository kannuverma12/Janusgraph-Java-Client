package com.paytm.digital.education.form.dao;

import com.paytm.digital.education.form.model.PaymentPostingError;
import com.paytm.digital.education.form.request.PaymentPostingItemRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class PaymentPostingErrorDao {

    private MongoOperations mongoOperations;

    public void upsertRecord(String refId, String errorMessage, String requestId,
                             PaymentPostingItemRequest paymentPostingRequest) {
        Update update = new Update();
        update.set("updatedAt", new Date());
        update.set("refId", refId);
        update.set("request", paymentPostingRequest);
        update.set("errorMessage", errorMessage);
        update.set("requestId", requestId);
        if (refId != null) {
            mongoOperations.upsert(new Query(Criteria.where("refId").is(refId)), update,
                    PaymentPostingError.class);
        } else {
            mongoOperations.upsert(new Query(Criteria.where("requestId").is(requestId)), update,
                    PaymentPostingError.class);
        }

    }

}
