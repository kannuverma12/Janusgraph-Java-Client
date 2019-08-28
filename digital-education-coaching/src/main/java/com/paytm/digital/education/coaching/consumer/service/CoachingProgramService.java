package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingProgramDetailsResponse;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingProgramTransformer;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.CoachingProgramEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mongodb.QueryOperators.AND;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_PROGRAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_TOP_RANKER_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_FIELD_GROUP;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_PROGRAM_ID_AND_URL_DISPLAY_KEY;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingProgramService {

    private static final String TARGET_EXAM    = "TARGET_EXAM";
    private static final String AUXILIARY_EXAM = "AUXILIARY_EXAM";

    private final CoachingProgramTransformer coachingProgramTransformer;
    private final CommonMongoRepository      commonMongoRepository;

    public GetCoachingProgramDetailsResponse getProgramDetailsByIdAndUrlDisplayKey(
            final long programId, final String urlDisplayKey) {
        final List<String> groupFields = this.commonMongoRepository.getFieldsByGroup(
                CoachingProgramEntity.class, DETAILS_FIELD_GROUP);
        if (CollectionUtils.isEmpty(groupFields)) {
            log.error("Got empty groupFields for: {}", DETAILS_FIELD_GROUP);
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }

        final List<String> programFields = new ArrayList<>();
        final List<String> examFields = new ArrayList<>();
        final List<String> topRankerFields = new ArrayList<>();
        final List<String> instituteFields = new ArrayList<>();
        this.fillFields(groupFields, programFields, examFields, topRankerFields, instituteFields);

        final CoachingProgramEntity program = this.fetchProgram(programId, urlDisplayKey,
                programFields);
        if (program == null) {
            throw new BadRequestException(INVALID_PROGRAM_ID_AND_URL_DISPLAY_KEY,
                    INVALID_PROGRAM_ID_AND_URL_DISPLAY_KEY.getExternalMessage());
        }

        final CoachingInstituteEntity institute = this.fetchInstitute(
                program.getCoachingInstituteId(), instituteFields);
        if (institute == null) {
            log.warn("Got null CoachingInstitute for id: {}, programId: {}",
                    program.getCoachingInstituteId(), programId);
            return null;
        }

        return this.buildResponse(program, institute,
                this.fetchExamTypeAndExamListMap(program.getPrimaryExamIds(),
                        program.getAuxiliaryExamIds(), examFields),
                this.fetchTopRankers(programId, topRankerFields));
    }

    private void fillFields(final List<String> groupFields, final List<String> programFields,
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
                    programFields.add(requestedField);
                }
            }
        }
    }

    private CoachingProgramEntity fetchProgram(final long programId,
            final String urlDisplayKey, final List<String> fields) {

        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(COACHING_PROGRAM_ID, programId);

        final List<CoachingProgramEntity> coachingProgramEntityList = this.commonMongoRepository
                .findAll(searchRequest, CoachingProgramEntity.class, fields, AND);

        if (CollectionUtils.isEmpty(coachingProgramEntityList)
                || coachingProgramEntityList.size() > 1
                || !urlDisplayKey.equalsIgnoreCase(CommonUtils.convertNameToUrlDisplayName(
                coachingProgramEntityList.get(0).getName()))) {
            log.error("Got no coachingProgram for ProgramId: {}, urlDisplayKey: {}",
                    programId, urlDisplayKey);
            return null;
        }
        return coachingProgramEntityList.get(0);
    }

    private CoachingInstituteEntity fetchInstitute(final long coachingInstituteId,
            final List<String> fields) {
        return this.commonMongoRepository.getEntityByFields(INSTITUTE_ID, coachingInstituteId,
                CoachingInstituteEntity.class, fields);
    }

    private Map<String, List<Exam>> fetchExamTypeAndExamListMap(List<Long> targetExamIdList,
            List<Long> auxiliaryExamIdList, final List<String> fields) {
        final List<Long> examIdList = new ArrayList<>();
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
                        .add(this.coachingProgramTransformer.convertExam(exam));
            } else if (auxiliaryExamIdsSet.contains(exam.getExamId())) {
                examTypeAndExamListMap.get(AUXILIARY_EXAM)
                        .add(this.coachingProgramTransformer.convertExam(exam));
            }
        }
        return examTypeAndExamListMap;
    }

    private List<com.paytm.digital.education.database.entity.Exam> fetchExamsByExamIds(
            final List<Long> examIdList, List<String> fields) {
        return this.commonMongoRepository.getEntityFieldsByValuesIn(EXAM_ID, examIdList,
                com.paytm.digital.education.database.entity.Exam.class, fields);
    }

    private List<TopRankerEntity> fetchTopRankers(final long programId, final List<String> fields) {
        List<TopRankerEntity> topRankerEntityList = this.commonMongoRepository
                .getEntitiesByIdAndFields(COACHING_PROGRAM_ID, programId, TopRankerEntity.class,
                        fields);
        if (CollectionUtils.isEmpty(topRankerEntityList)) {
            log.error("Got no topRankers for ProgramId: {}", programId);
            return new ArrayList<>();
        }
        return topRankerEntityList;
    }

    private GetCoachingProgramDetailsResponse buildResponse(
            final CoachingProgramEntity program, final CoachingInstituteEntity institute,
            final Map<String, List<Exam>> examTypeAndExamListMap,
            final List<TopRankerEntity> topRankers) {

        return GetCoachingProgramDetailsResponse.builder()
                .coachingInstituteId(program.getCoachingInstituteId())
                .coachingInstituteName(institute.getBrandName())
                .programId(program.getProgramId())
                .programName(program.getName())
                .programType(program.getCourseType())
                .programLogo(institute.getLogo())
                .programDescription(program.getDescription())
                .programPrice(program.getPrice())
                .currency(program.getCurrency())
                .targetExams(examTypeAndExamListMap.get(TARGET_EXAM))
                .auxiliaryExams(examTypeAndExamListMap.get(AUXILIARY_EXAM))
                .eligibility(program.getEligibility())
                .duration(program.getDuration())
                .topRankers(this.coachingProgramTransformer.convertTopRankers(topRankers))
                .importantDates(this.coachingProgramTransformer.convertImportantDates(
                        program.getImportantDates()))
                .programFeatures(this.coachingProgramTransformer.convertProgramFeatures(
                        program.getFeatures()))
                .programInclusions(program.getInclusions())
                .sessionDetails(this.coachingProgramTransformer.convertSessionDetails(
                        program.getSessionDetails()))
                .syllabus(program.getSyllabus())
                .brochure(program.getBrochure())
                .build();
    }
}
