package com.paytm.digital.education.predictor.repository;

import com.paytm.digital.education.predictor.model.PredictorStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictorStatsRepository extends MongoRepository<PredictorStats, String> {

    public PredictorStats findByCustomerIdAndMerchantProductIdAndMerchantId(String customerId,
            String merchantProductId, String merchantId);
}
