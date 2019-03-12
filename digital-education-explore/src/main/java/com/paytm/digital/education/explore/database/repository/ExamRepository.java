package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ExamRepository extends MongoRepository<Exam, Long> {

    Exam findByExamId(Long id);

}
