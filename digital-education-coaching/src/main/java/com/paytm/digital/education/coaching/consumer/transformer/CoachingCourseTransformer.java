package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourseImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourseSessionDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.coaching.enums.CourseSessionDetails;
import com.paytm.digital.education.database.embedded.CoachingCourseFeature;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CoachingCourseTransformer {

    public List<TopRanker> convertTopRankers(final List<TopRankerEntity> topRankerEntityList,
            final Map<Long, String> examIdAndNameMap, final Map<Long, String> courseIdAndNameMap) {
        if (CollectionUtils.isEmpty(topRankerEntityList)) {
            return new ArrayList<>();
        }
        return topRankerEntityList.stream()
                .map(tr -> TopRanker.builder()
                        .id(tr.getTopRankerId())
                        .coachingInstituteId(tr.getInstituteId())
                        .coachingCentreId(tr.getCenterId())
                        .coachingCourseNames(this.getCourseNameFromIds(tr.getCourseIds(),
                                courseIdAndNameMap))
                        .examName(examIdAndNameMap.getOrDefault(tr.getExamId(), null))
                        .studentName(tr.getStudentName())
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
                .examFullName(exam.getExamFullName())
                .conductedBy(exam.getConductingBody())
                .build();
    }

    public List<CoachingCourseImportantDate> convertImportantDates(
            final List<com.paytm.digital.education.database.embedded.CoachingCourseImportantDate>
                    impDates) {
        if (CollectionUtils.isEmpty(impDates)) {
            return new ArrayList<>();
        }
        return impDates.stream()
                .map(date -> CoachingCourseImportantDate.builder()
                        .key(date.getKey())
                        .value(date.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CoachingCourseFeature> convertCourseFeatures(
            final List<CoachingCourseFeature> features) {
        if (CollectionUtils.isEmpty(features)) {
            return new ArrayList<>();
        }
        return features.stream()
                .map(feature -> CoachingCourseFeature.builder()
                        .featureId(feature.getFeatureId())
                        .featureName(feature.getFeatureName())
                        .featureLogo(feature.getFeatureLogo())
                        .featureDescription(feature.getFeatureDescription())
                        .priority(feature.getPriority())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CoachingCourseSessionDetails> convertSessionDetails(
            final CoachingCourseEntity course) {
        final List<CoachingCourseSessionDetails> sessionDetails = new ArrayList<>();

        for (final CourseSessionDetails.Session session :
                CourseSessionDetails.getCourseTypeAndSessionsMap().get(course.getCourseType())) {

            String value;
            try {
                final Field field = CoachingCourseEntity.class.getField(session.getDbFieldName());
                value =  ((Integer) field.get(course)).toString();
            } catch (final Exception ex) {
                log.error("Got exception, course: {}", course, ex);
                continue;
            }

            sessionDetails.add(CoachingCourseSessionDetails.builder()
                    .key(session.getDisplayName())
                    .value(value)
                    .build());

        }
        return sessionDetails;
    }

    private List<String> getCourseNameFromIds(final List<Long> courseIds,
            final Map<Long, String> courseIdAndNameMap) {
        if (courseIds.isEmpty() || courseIdAndNameMap.isEmpty()) {
            return new ArrayList<>();
        }

        final List<String> courseNames = new ArrayList<>();
        for (Map.Entry<Long, String> entry : courseIdAndNameMap.entrySet()) {
            if (courseIds.contains(entry.getKey())) {
                courseNames.add(entry.getValue());
            }
        }
        return courseNames;
    }
}
