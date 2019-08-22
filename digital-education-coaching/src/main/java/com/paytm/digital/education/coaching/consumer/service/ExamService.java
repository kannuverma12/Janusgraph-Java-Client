package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.response.GetExamDetailsResponse;
import com.paytm.digital.education.coaching.db.dao.ExamDAO;
import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ExamAdditionalInfoParams;

@Slf4j
@Service
public class ExamService {

    @Autowired private ExamDAO examDAO;

    public GetExamDetailsResponse getExamDetails(final Long examId, final String urlDisplayKey) {
        Exam exam = examDAO.findByExamId(examId);

        if (Objects.isNull(exam)) {
            log.error("Exam with id: {} does not exist", examId);
            throw new InvalidRequestException(new IllegalArgumentException("Invalid Exam id"),
                    ErrorCode.DP_INVALID_REQUEST, "Invalid Exam id", "Exam id not present");
        }

        if (!CommonUtils.convertNameToUrlDisplayName(exam.getExamFullName())
                .equals(urlDisplayKey)) {
            log.error("Exam with url display key: {} does not exist", urlDisplayKey);
            throw new InvalidRequestException(
                    new IllegalArgumentException("Invalid url display key"),
                    ErrorCode.DP_INVALID_REQUEST, "Invalid url display key",
                    "Url display key is not valid");
        }

        return GetExamDetailsResponse.builder()
                .examId(exam.getExamId())
                .examFullName(exam.getExamFullName())
                .examShortName(exam.getExamShortName())
                .urlDisplayKey(urlDisplayKey)
                .examDescription(exam.getAboutExam())
                .additionalInfo(ExamAdditionalInfoParams)
                .build();
    }
}
