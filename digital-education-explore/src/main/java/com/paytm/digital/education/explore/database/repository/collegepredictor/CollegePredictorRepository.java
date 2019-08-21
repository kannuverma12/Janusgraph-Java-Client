package com.paytm.digital.education.explore.database.repository.collegepredictor;

import com.paytm.digital.education.explore.database.entity.collegepredictor.CollegePredictor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegePredictorRepository extends MongoRepository<CollegePredictor, String> {

    Optional<CollegePredictor> findCollegePredictorByMerchantSku(Long id);
}
