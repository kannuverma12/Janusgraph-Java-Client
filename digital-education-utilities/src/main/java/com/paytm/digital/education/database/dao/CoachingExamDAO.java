package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.CoachingExamRepositoryNew;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingExamDAO {

    @Autowired
    CoachingExamRepositoryNew coachingCenterRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    public CoachingExamEntity save(@NonNull CoachingExamEntity coachingExamEntity) {
        if (Objects.isNull(coachingExamEntity.getCoachingExamId())) {
            coachingExamEntity.setCoachingExamId(sequenceGenerator
                    .getNextSequenceId(coachingExamEntity.getClass().getSimpleName()));
        }
        return coachingCenterRepository.save(coachingExamEntity);
    }

    public CoachingExamEntity findByExamId(@NonNull Long id) {
        return coachingCenterRepository.findByCoachingExamId(id);
    }

    public List<CoachingExamEntity> findAll() {
        return this.coachingCenterRepository.findAll();
    }

    public List<Exam> findByExamIdsIn(String examIdField,
            List<Long> examIds,
            List<String> projectionFields) {
        return commonMongoRepository.getEntityFieldsByValuesIn(examIdField, examIds,
                Exam.class, projectionFields);
    }

    public Exam findByExamId(String examIdField, long examId,
            List<String> projectionFields) {
        return commonMongoRepository.getEntityByFields(
                examIdField, examId, Exam.class, projectionFields);
    }

}
