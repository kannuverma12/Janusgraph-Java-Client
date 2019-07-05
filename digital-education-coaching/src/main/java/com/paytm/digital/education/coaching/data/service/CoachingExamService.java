package com.paytm.digital.education.coaching.data.service;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_NOT_FOUND_ERROR;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SUCCESS_MESSAGE;

import com.paytm.digital.education.coaching.database.entity.CoachingExam;
import com.paytm.digital.education.coaching.database.repository.CoachingExamRepository;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CoachingExamService {

    private CoachingExamRepository coachingExamRepository;

    private static ResponseDto examNotFoundResponse =
            new ResponseDto(404, EXAM_NOT_FOUND_ERROR);
    private static ResponseDto successResponse      = new ResponseDto(200, SUCCESS_MESSAGE);

    public ResponseDto createCoachingExam(CoachingExam coachingExam) {
        return coachingExamRepository.createCoachingExam(coachingExam);
    }

    public ResponseDto updateCoachingExam(CoachingExam coachingExam) {
        return coachingExamRepository.updateCoachingExam(coachingExam);
    }

    public ResponseDto updateCoachingExamStatus(long examId, boolean active) {
        coachingExamRepository.updateCoachingExamStatus(examId, active);
        return successResponse;
    }

    public ResponseDto getCoachingExamById(long examId, Boolean active) {
        CoachingExam coachingExam = coachingExamRepository.findCoachingExamById(examId, active);
        if (Objects.isNull(coachingExam)) {
            return examNotFoundResponse;
        }
        return coachingExam;
    }

}
