package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.repository.CoachingProgramRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CoachingCourseDAO {

    @Autowired
    CoachingProgramRepository programRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingCourseEntity save(@NonNull CoachingCourseEntity coachingProgramEntity) {
        if (Objects.nonNull(coachingProgramEntity.getCourseId())) {
            coachingProgramEntity.setCourseId(sequenceGenerator
                    .getNextSequenceId(coachingProgramEntity.getClass().getSimpleName()));
        }
        return programRepository.save(coachingProgramEntity);
    }

    public CoachingCourseEntity findByProgramId(@NonNull Long id) {
        return programRepository.findByCourseId(id);
    }
}
