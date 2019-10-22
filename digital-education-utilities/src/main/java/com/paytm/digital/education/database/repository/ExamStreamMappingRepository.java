package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.ExamStreamEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamStreamMappingRepository extends MongoRepository<ExamStreamEntity, ObjectId> {

    ExamStreamEntity findByExamIdAndAndMerchantStreamAndPaytmStream(Long examId,
            String merchantStream, String paytmStream);
}
