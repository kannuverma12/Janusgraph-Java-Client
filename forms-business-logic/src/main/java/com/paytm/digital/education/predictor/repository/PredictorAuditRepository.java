package com.paytm.digital.education.predictor.repository;

import com.paytm.digital.education.predictor.model.PredictorAuditLogs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PredictorAuditRepository extends MongoRepository<PredictorAuditLogs, String> {

    PredictorAuditLogs findByRefId(String refId);
}
