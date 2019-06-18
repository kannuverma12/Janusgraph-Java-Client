package com.paytm.digital.education.predictor.repository;

import com.paytm.digital.education.predictor.model.CollegePredictor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictorListRepository extends MongoRepository<CollegePredictor, String> {

}
