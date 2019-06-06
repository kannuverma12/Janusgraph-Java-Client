package com.paytm.digital.education.form.repository;

import com.paytm.digital.education.form.model.CollegePredictor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictorListRepository extends MongoRepository<CollegePredictor, String> {

}
