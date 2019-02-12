package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Institute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public interface CollegeRepository extends MongoRepository<Institute, String> {

    Institute findByInstituteId(long id);

}
