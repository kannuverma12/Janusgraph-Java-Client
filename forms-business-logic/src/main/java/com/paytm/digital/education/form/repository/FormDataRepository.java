package com.paytm.digital.education.form.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.paytm.digital.education.form.model.FormData;

@Transactional
@Repository
public interface FormDataRepository extends MongoRepository<FormData, String> {
}
