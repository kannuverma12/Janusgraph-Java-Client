package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingCourseDetailsResponse;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingCourseTransformer;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mongodb.QueryOperators.AND;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_TOP_RANKER_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_FIELD_GROUP;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_COURSE_ID_AND_URL_DISPLAY_KEY;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCourseService {

    private static final String TARGET_EXAM     = "TARGET_EXAM";
    private static final String AUXILIARY_EXAM  = "AUXILIARY_EXAM";
    private static final String TOP_RANKER_EXAM = "TOP_RANKER_EXAM";

    private final CoachingCourseTransformer coachingCourseTransformer;
    private final CommonMongoRepository     commonMongoRepository;

    public GetCoachingCourseDetailsResponse getCourseDetailsByIdAndUrlDisplayKey(
            final long courseId, final String urlDisplayKey) {
        final List<String> groupFields = this.commonMongoRepository.getFieldsByGroup(
                CoachingCourseEntity.class, DETAILS_FIELD_GROUP);
        if (CollectionUtils.isEmpty(groupFields)) {
            log.error("Got empty groupFields for: {}", DETAILS_FIELD_GROUP);
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }

        final List<String> courseFields = new ArrayList<>();
        final List<String> examFields = new ArrayList<>();
        final List<String> topRankerFields = new ArrayList<>();
        final List<String> instituteFields = new ArrayList<>();
        this.fillFields(groupFields, courseFields, examFields, topRankerFields, instituteFields);

        final CoachingCourseEntity course = this.fetchCourse(courseId, urlDisplayKey,
                courseFields);
        if (course == null) {
            throw new BadRequestException(INVALID_COURSE_ID_AND_URL_DISPLAY_KEY,
                    INVALID_COURSE_ID_AND_URL_DISPLAY_KEY.getExternalMessage());
        }

        final CoachingInstituteEntity institute = this.fetchInstitute(
                course.getCoachingInstituteId(), instituteFields);
        if (institute == null) {
            log.warn("Got null CoachingInstitute for id: {}, courseId: {}",
                    course.getCoachingInstituteId(), courseId);
            return null;
        }

        final List<TopRankerEntity> topRankerEntityList = this.fetchTopRankers(courseId,
                topRankerFields, institute.getInstituteId());

        final Map<Long, String> examIdAndNameMap = new HashMap<>();
        Map<Long, String> courseIdAndNameMap = new HashMap<>();
        final List<Long> topRankerExamIds = new ArrayList<>();

        if (!CollectionUtils.isEmpty(topRankerEntityList)) {
            final List<Long> courseIds = new ArrayList<>();
            for (final TopRankerEntity tr : topRankerEntityList) {
                topRankerExamIds.add(tr.getExamId());
                courseIds.addAll(tr.getCourseIds());
            }
            courseIdAndNameMap = getCourseIdAndNameMap(courseIds);
        }

        final Map<String, List<Exam>> examTypeAndExamListMap = this.fetchExamTypeAndExamListMap(
                course.getPrimaryExamIds(), course.getAuxiliaryExamIds(), examFields,
                topRankerExamIds);

        if (!examTypeAndExamListMap.isEmpty()) {
            final List<Exam> topRankerExams = examTypeAndExamListMap.get(TOP_RANKER_EXAM);
            if (!topRankerExams.isEmpty()) {
                for (final Exam exam : topRankerExams) {
                    examIdAndNameMap.put(exam.getId(), exam.getExamShortName());
                }
            }
        }

        return this.buildResponse(course, institute, examTypeAndExamListMap, topRankerEntityList,
                examIdAndNameMap, courseIdAndNameMap);
    }

    private void fillFields(final List<String> groupFields, final List<String> courseFields,
            final List<String> examFields, final List<String> topRankerFields,
            final List<String> instituteFields) {
        if (!CollectionUtils.isEmpty(groupFields)) {

            for (String requestedField : groupFields) {

                if (requestedField.contains(EXAM_PREFIX)) {
                    examFields.add(requestedField.substring(EXAM_PREFIX.length()));

                } else if (requestedField.contains(COACHING_TOP_RANKER_PREFIX)) {
                    topRankerFields.add(requestedField.substring(
                            COACHING_TOP_RANKER_PREFIX.length()));

                } else if (requestedField.contains(COACHING_INSTITUTE_PREFIX)) {
                    instituteFields.add(requestedField.substring(
                            COACHING_INSTITUTE_PREFIX.length()));

                } else {
                    courseFields.add(requestedField);
                }
            }
        }
    }

    private CoachingCourseEntity fetchCourse(final long courseId,
            final String urlDisplayKey, final List<String> fields) {

        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(COACHING_COURSE_ID, courseId);

        final List<CoachingCourseEntity> coachingCourseEntityList = this.commonMongoRepository
                .findAll(searchRequest, CoachingCourseEntity.class, fields, AND);

        if (CollectionUtils.isEmpty(coachingCourseEntityList)
                || coachingCourseEntityList.size() > 1
                || !urlDisplayKey.equalsIgnoreCase(CommonUtils.convertNameToUrlDisplayName(
                coachingCourseEntityList.get(0).getName()))) {
            log.error("Got no coachingCourse for courseId: {}, urlDisplayKey: {}",
                    courseId, urlDisplayKey);
            return null;
        }
        return coachingCourseEntityList.get(0);
    }

    private CoachingInstituteEntity fetchInstitute(final long coachingInstituteId,
            final List<String> fields) {
        return this.commonMongoRepository.getEntityByFields(INSTITUTE_ID, coachingInstituteId,
                CoachingInstituteEntity.class, fields);
    }

    private Map<Long, String> getCourseIdAndNameMap(final List<Long> coachingCourseIdList) {

        List<String> fields = new ArrayList<>();
        fields.add(NAME);
        fields.add(COURSE_ID);

        final List<CoachingCourseEntity> coachingCourseEntityList = this.fetchCourses(
                coachingCourseIdList, fields);

        if (CollectionUtils.isEmpty(coachingCourseEntityList)) {
            return new HashMap<>();
        }

        final Map<Long, String> courseIdAndNameMap = new HashMap<>();

        for (CoachingCourseEntity course : coachingCourseEntityList) {
            courseIdAndNameMap.put(course.getCourseId(), course.getName());
        }
        return courseIdAndNameMap;
    }

    private List<CoachingCourseEntity> fetchCourses(final List<Long> coachingCourseIdList,
            final List<String> fields) {
        return this.commonMongoRepository.getEntityFieldsByValuesIn(COURSE_ID, coachingCourseIdList,
                CoachingCourseEntity.class, fields);
    }

    private Map<String, List<Exam>> fetchExamTypeAndExamListMap(List<Long> targetExamIdList,
            List<Long> auxiliaryExamIdList, final List<String> fields,
            final List<Long> topRankerExamIds) {
        final List<Long> examIdList = new ArrayList<>();
        examIdList.addAll(topRankerExamIds);
        if (!CollectionUtils.isEmpty(targetExamIdList)) {
            examIdList.addAll(targetExamIdList);
        } else {
            targetExamIdList = new ArrayList<>();
        }

        if (!CollectionUtils.isEmpty(auxiliaryExamIdList)) {
            examIdList.addAll(auxiliaryExamIdList);
        } else {
            auxiliaryExamIdList = new ArrayList<>();
        }

        final Map<String, List<Exam>> examTypeAndExamListMap = new HashMap<>();
        examTypeAndExamListMap.put(TARGET_EXAM, new ArrayList<>());
        examTypeAndExamListMap.put(AUXILIARY_EXAM, new ArrayList<>());
        examTypeAndExamListMap.put(TOP_RANKER_EXAM, new ArrayList<>());

        final List<com.paytm.digital.education.database.entity.Exam> exams =
                this.fetchExamsByExamIds(examIdList, fields);
        if (CollectionUtils.isEmpty(exams)) {
            log.warn("Got no exams for examIds: {}", examIdList);
            return examTypeAndExamListMap;
        }

        final Set<Long> targetExamIdsSet = new HashSet<>(targetExamIdList);
        final Set<Long> auxiliaryExamIdsSet = new HashSet<>(auxiliaryExamIdList);
        for (final com.paytm.digital.education.database.entity.Exam exam : exams) {
            if (targetExamIdsSet.contains(exam.getExamId())) {
                examTypeAndExamListMap.get(TARGET_EXAM)
                        .add(this.coachingCourseTransformer.convertExam(exam));
            } else if (auxiliaryExamIdsSet.contains(exam.getExamId())) {
                examTypeAndExamListMap.get(AUXILIARY_EXAM)
                        .add(this.coachingCourseTransformer.convertExam(exam));
            }
            if (topRankerExamIds.contains(exam.getExamId())) {
                examTypeAndExamListMap.get(TOP_RANKER_EXAM)
                        .add(this.coachingCourseTransformer.convertExam(exam));
            }
        }
        return examTypeAndExamListMap;
    }

    private List<com.paytm.digital.education.database.entity.Exam> fetchExamsByExamIds(
            final List<Long> examIdList, List<String> fields) {
        return this.commonMongoRepository.getEntityFieldsByValuesIn(EXAM_ID, examIdList,
                com.paytm.digital.education.database.entity.Exam.class, fields);
    }

    private List<TopRankerEntity> fetchTopRankers(final long courseId, final List<String> fields,
            Long instituteId) {
        List<TopRankerEntity> topRankerEntityList = this.commonMongoRepository
                .getEntityFieldsByValuesIn(COACHING_COURSE_IDS,
                        Collections.singletonList(courseId), TopRankerEntity.class, fields);
        if (CollectionUtils.isEmpty(topRankerEntityList)) {
            log.error("Got no topRankers for courseId: {}", courseId);

            topRankerEntityList = this.commonMongoRepository.getEntityFieldsByValuesIn(INSTITUTE_ID,
                    Collections.singletonList(instituteId), TopRankerEntity.class, fields);
            if (CollectionUtils.isEmpty(topRankerEntityList)) {
                log.error("Got no topRankers for instituteId: {}", instituteId);
                return new ArrayList<>();
            }
        }
        return topRankerEntityList;
    }

    private GetCoachingCourseDetailsResponse buildResponse(
            final CoachingCourseEntity course, final CoachingInstituteEntity institute,
            final Map<String, List<Exam>> examTypeAndExamListMap,
            final List<TopRankerEntity> topRankers, final Map<Long, String> examIdAndNameMap,
            final Map<Long, String> courseIdAndNameMap) {

        return GetCoachingCourseDetailsResponse.builder()
                .coachingInstituteId(course.getCoachingInstituteId())
                .coachingInstituteName(institute.getBrandName())
                .courseId(course.getCourseId())
                .courseName(course.getName())
                .courseType(course.getCourseType())
                .courseLogo(institute.getLogo())
                .courseDescription(course.getDescription())
                .coursePrice(course.getPrice())
                .currency(course.getCurrency())
                .targetExams(examTypeAndExamListMap.get(TARGET_EXAM))
                .auxiliaryExams(examTypeAndExamListMap.get(AUXILIARY_EXAM))
                .eligibility(course.getEligibility())
                .duration(course.getDuration())
                .topRankers(this.coachingCourseTransformer.convertTopRankers(topRankers,
                        examIdAndNameMap, courseIdAndNameMap))
                .importantDates(this.coachingCourseTransformer.convertImportantDates(
                        course.getImportantDates()))
                .sessionDetails(this.coachingCourseTransformer.convertSessionDetails(course))
                //.courseFeatures(this.coachingCourseTransformer.convertCourseFeatures(course.getFeatures()))
                .syllabus(course.getSyllabus())
                .brochure(course.getBrochure())
                .build();
    }
}
