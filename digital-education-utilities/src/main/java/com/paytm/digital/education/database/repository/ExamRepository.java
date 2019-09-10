package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ExamRepository extends MongoRepository<Exam, Long> {

    Exam findByExamId(Long id);

    @Query(value = "{exam_id: { $in: ?0 } })", fields = "{'exam_id':1, _id : 0}")
    List<Exam> findAllByExamId(List<Long> ids);

}
