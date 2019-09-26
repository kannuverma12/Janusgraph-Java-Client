package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFeature;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSE_FEATURE;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_RANKER;

@Slf4j
@Component
public class CoachingCourseTransformer {

    private static final String YES = "Yes";
    private static final String NO  = "No";

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
                        .examName(examIdAndNameMap.getOrDefault(tr.getExamId(), EMPTY_STRING))
                        .studentName(tr.getStudentName())
                        .image(CommonUtil.getAbsoluteUrl(tr.getStudentPhoto(), COACHING_TOP_RANKER))
                        .rank(tr.getRankObtained())
                        .examYear(tr.getExamYear())
                        .testimonial(tr.getTestimonial())
                        .build())
                .collect(Collectors.toList());
    }

    public Exam convertExam(final com.paytm.digital.education.database.entity.Exam exam) {
        return Exam.builder()
                .id(exam.getExamId())
                .examFullName(exam.getExamFullName())
                .examShortName(exam.getExamShortName())
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
            final List<CoachingCourseFeatureEntity> features) {
        if (CollectionUtils.isEmpty(features)) {
            return Collections.emptyList();
        }
        return features.stream()
                .map(feature -> CoachingCourseFeature.builder()
                        .featureId(feature.getCoachingCourseFeatureId())
                        .featureName(feature.getName())
                        .featureLogo(CommonUtil.getAbsoluteUrl(feature.getLogo(),
                                COACHING_COURSE_FEATURE))
                        .featureDescription(feature.getDescription())
                        .priority(feature.getPriority())
                        .build())
                .collect(Collectors.toList());
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

    public String convertBooleanToString(final Boolean value) {
        if (null == value) {
            return NO;
        }
        return value ? YES : NO;
    }
}
