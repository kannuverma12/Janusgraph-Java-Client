package com.paytm.digital.education.coaching.data.service;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_NOT_FOUND_ERROR;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SUCCESS_MESSAGE;

import com.paytm.digital.education.coaching.database.entity.CoachingCourse;
import com.paytm.digital.education.coaching.database.repository.CoachingCourseRepository;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CoachingCourseService {
    @Autowired
    private CoachingCourseRepository coachingCourseRepository;

    private ResponseDto successResponse        = new ResponseDto(200, SUCCESS_MESSAGE);
    private ResponseDto courseNotFoundResponse = new ResponseDto(404, COURSE_NOT_FOUND_ERROR);

    public ResponseDto createCourse(CoachingCourse coachingCourse) {
        return coachingCourseRepository.createCourse(coachingCourse);
    }

    public ResponseDto updateCourse(CoachingCourse coachingCourse) {
        return coachingCourseRepository.updateCourse(coachingCourse);
    }

    public ResponseDto updateCourseStatus(long courseId, boolean activate) {
        coachingCourseRepository.updateCourseStatus(courseId, activate);
        return successResponse;
    }

    public ResponseDto getCourseById(long courseId, Boolean active) {
        CoachingCourse coachingCourse =
                coachingCourseRepository.getCoachingCourseById(courseId, active);
        if (Objects.isNull(coachingCourse)) {
            return courseNotFoundResponse;
        }
        return coachingCourse;
    }
}
