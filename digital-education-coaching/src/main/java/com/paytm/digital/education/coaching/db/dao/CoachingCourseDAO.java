package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.repository.CoachingProgramRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingCourseDAO {

    @Autowired
    CoachingProgramRepository programRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingCourseEntity save(@NonNull CoachingCourseEntity coachingProgramEntity) {
        if (Objects.isNull(coachingProgramEntity.getCourseId())) {
            coachingProgramEntity.setCourseId(sequenceGenerator
                    .getNextSequenceId(coachingProgramEntity.getClass().getSimpleName()));
        }
        return programRepository.save(coachingProgramEntity);
    }

    public List<CoachingCourseEntity> findAllByCourseId(@NonNull List<Long> ids) {
        return programRepository.findAllByCourseId(ids);
    }

    public CoachingCourseEntity findByProgramId(@NonNull Long id) {
        return programRepository.findByCourseId(id);
    }

    public List<CoachingCourseEntity> findAll() {
        return this.programRepository.findAll();
    }

}
