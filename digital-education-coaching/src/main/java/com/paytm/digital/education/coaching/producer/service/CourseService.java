package com.paytm.digital.education.coaching.producer.service;


import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.repository.CoachingCourseRepository;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseCreateRequest;
import com.paytm.digital.education.coaching.producer.transformer.CourseTransformer;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourseService {

    @Autowired
    private CoachingCourseRepository coachingCourseRepository;

    @Autowired
    private CourseTransformer courseTransformer;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public Long save(CoachingCourseCreateRequest coachingCourseCreateRequest) {
        CoachingCourseEntity coachingCourseEntity =
            courseTransformer.transformProgramCreateRequestToProgramEntity(
                    coachingCourseCreateRequest, new CoachingCourseEntity());
        coachingCourseEntity.setCourseId(sequenceGenerator.getNextSequenceId(CoachingConstants.COURSE));
        coachingCourseEntity = coachingCourseRepository.save(coachingCourseEntity);
        if (Objects.nonNull(coachingCourseEntity)) {
            return coachingCourseEntity.getCoachingInstituteId();
        }
        return null;
    }

    public Long update(CoachingCourseCreateRequest coachingCourseCreateRequest) {

        if (Objects.isNull(coachingCourseCreateRequest.getId())) {
            return null;
        }

        CoachingCourseEntity coachingCourseEntity = null;
        Optional<CoachingCourseEntity> courseEntityOptional =
            coachingCourseRepository.findByCourseId(coachingCourseCreateRequest.getId());
        if (courseEntityOptional.isPresent()) {
            coachingCourseEntity = courseEntityOptional.get();
            coachingCourseEntity =
                courseTransformer.transformProgramCreateRequestToProgramEntity(
                        coachingCourseCreateRequest, coachingCourseEntity);
            coachingCourseEntity = coachingCourseRepository.save(coachingCourseEntity);
        }
        if (Objects.nonNull(coachingCourseEntity)) {
            return coachingCourseEntity.getCoachingInstituteId();
        }
        return null;
    }
}
