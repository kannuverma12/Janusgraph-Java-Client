package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.ExamLogo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamLogoRepository extends MongoRepository<ExamLogo, String> {

    public ExamLogo findByExamId(Long examId);

}
