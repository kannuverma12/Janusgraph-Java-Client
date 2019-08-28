package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgramFeature;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgramImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgramSessionDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CoachingProgramTransformer {

    public List<TopRanker> convertTopRankers(final List<TopRankerEntity> topRankerEntityList) {
        if (CollectionUtils.isEmpty(topRankerEntityList)) {
            return new ArrayList<>();
        }
        return topRankerEntityList.stream()
                .map(tr -> TopRanker.builder()
                        .id(tr.getTopRankerId())
                        .coachingInstituteId(tr.getInstituteId())
                        .coachingCentreId(tr.getCenterId())
                        .coachingProgramId(tr.getProgramId())
                        .examId(tr.getExamId())
                        .name(tr.getStudentName())
                        .image(tr.getStudentPhoto())
                        .rank(tr.getRankObtained())
                        .examDate(tr.getExamYear())
                        .testimonial(tr.getTestimonial())
                        .build())
                .collect(Collectors.toList());
    }

    public Exam convertExam(final com.paytm.digital.education.database.entity.Exam exam) {
        return Exam.builder()
                .id(exam.getExamId())
                .name(exam.getExamFullName())
                .conductedBy(exam.getConductingBody())
                .build();
    }

    public List<CoachingProgramImportantDate> convertImportantDates(
            final List<com.paytm.digital.education.database.embedded.CoachingProgramImportantDate> impDates) {
        if (CollectionUtils.isEmpty(impDates)) {
            return new ArrayList<>();
        }
        return impDates.stream()
                .map(date -> CoachingProgramImportantDate.builder()
                        .key(date.getKey())
                        .value(date.getValue())
                        .priority(date.getPriority())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CoachingProgramFeature> convertProgramFeatures(
            final List<com.paytm.digital.education.database.embedded.CoachingProgramFeature> features) {
        if (CollectionUtils.isEmpty(features)) {
            return new ArrayList<>();
        }
        return features.stream()
                .map(feature -> CoachingProgramFeature.builder()
                        .featureId(feature.getFeatureId())
                        .featureName(feature.getFeatureName())
                        .featureLogo(feature.getFeatureLogo())
                        .featureDescription(feature.getFeatureDescription())
                        .priority(feature.getPriority())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CoachingProgramSessionDetails> convertSessionDetails(
            final List<com.paytm.digital.education.database.embedded.CoachingProgramSessionDetails> sessionDetails) {
        if (CollectionUtils.isEmpty(sessionDetails)) {
            return new ArrayList<>();
        }
        return sessionDetails.stream()
                .map(feature -> CoachingProgramSessionDetails.builder()
                        .key(feature.getKey())
                        .value(feature.getValue())
                        .priority(feature.getPriority())
                        .build())
                .collect(Collectors.toList());
    }
}
