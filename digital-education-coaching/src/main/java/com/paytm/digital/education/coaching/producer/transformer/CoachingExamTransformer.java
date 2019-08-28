package com.paytm.digital.education.coaching.producer.transformer;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamCreateRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class CoachingExamTransformer {

    private final SequenceGenerator sequenceGenerator;

    public CoachingExamEntity transform(final CoachingExamCreateRequest request) {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        return CoachingExamEntity.builder()
                .coachingExamId(
                        sequenceGenerator.getNextSequenceId(CoachingConstants.COACHING_EXAM))
                .instituteId(request.getInstituteId())
                .streamId(request.getStreamId())
                .courseId(request.getCourseId())
                .examType(request.getExamType())
                .examDate(request.getExamDate())
                .examDescription(request.getExamDescription())
                .examDuration(request.getExamDuration())
                .maximumMarks(request.getMaximumMarks())
                .eligibility(request.getEligibility())
                .active(request.getActive())
                .createdAt(currentTimestamp)
                .updatedAt(currentTimestamp)
                .build();
    }

    public CoachingExamEntity transform(
            final CoachingExamUpdateRequest request,
            final CoachingExamEntity existingCoachingExam) {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        return CoachingExamEntity.builder()
                .id(existingCoachingExam.getId())
                .coachingExamId(existingCoachingExam.getCoachingExamId())
                .instituteId(request.getInstituteId())
                .streamId(request.getStreamId())
                .courseId(request.getCourseId())
                .examType(request.getExamType())
                .examDate(request.getExamDate())
                .examDescription(request.getExamDescription())
                .examDuration(request.getExamDuration())
                .maximumMarks(request.getMaximumMarks())
                .eligibility(request.getEligibility())
                .active(request.getActive())
                .createdAt(existingCoachingExam.getCreatedAt())
                .updatedAt(currentTimestamp)
                .build();
    }
}
