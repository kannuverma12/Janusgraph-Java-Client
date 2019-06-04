package com.paytm.digital.education.form.repository;

import com.paytm.digital.education.form.model.FormData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface FormDataRepository extends MongoRepository<FormData, String> {

    @Query(value = "{'customerId':'?0','merchantId':'?1','formFulfilment.paymentStatus':"
            + "{$exists:true}, 'formFulfilment.paymentStatus':'?2', 'formFulfilment.amount':{$exists:true, $gt:0}}")
    public List<FormData> getFormsDataByPaymentStatus(String customerId, String merchantId, String paymentStatus);

}
