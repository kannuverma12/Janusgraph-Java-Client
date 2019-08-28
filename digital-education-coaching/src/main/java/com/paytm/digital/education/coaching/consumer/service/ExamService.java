package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCoursesForExam;
import com.paytm.digital.education.coaching.consumer.model.response.GetExamDetailsResponse;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_FIELD_GROUP;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ExamAdditionalInfoParams;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MMM_YYYY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.NON_TENTATIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.utility.DateUtil.stringToDate;

@Slf4j
@Service
@AllArgsConstructor
public class ExamService {

    private static final Date                  MAX_DATE = new Date(Long.MAX_VALUE);
    private static final Date                  MIN_DATE = new Date(Long.MIN_VALUE);
    private final        CommonMongoRepository commonMongoRepository;

    public GetExamDetailsResponse getExamDetails(final Long examId, final String urlDisplayKey) {
        List<String> groupFields = this.commonMongoRepository.getFieldsByGroup(
                Exam.class, DETAILS_FIELD_GROUP);
        if (CollectionUtils.isEmpty(groupFields)) {
            log.error("Group fields not found for entity: exam and group: {}", DETAILS_FIELD_GROUP);
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }

        List<String> examFields = new ArrayList<>();
        List<String> coachingInstituteFields = new ArrayList<>();
        List<String> coachingProgramFields = new ArrayList<>();

        this.getFieldsForEntities(groupFields, examFields, coachingInstituteFields,
                coachingProgramFields);

        Exam exam = this.commonMongoRepository.getEntityByFields(
                EXAM_ID, examId, Exam.class, examFields);
        if (Objects.isNull(exam)) {
            log.error("Exam with id: {} does not exist", examId);
            throw new BadRequestException(INVALID_EXAM_ID, INVALID_EXAM_ID.getExternalMessage());
        }
        if (!CommonUtils.convertNameToUrlDisplayName(exam.getExamFullName())
                .equals(urlDisplayKey)) {
            log.error("Exam with url display key: {} does not exist for exam_id: {}", urlDisplayKey,
                    examId);
            throw new BadRequestException(INVALID_EXAM_NAME,
                    INVALID_EXAM_NAME.getExternalMessage());
        }

        return GetExamDetailsResponse.builder()
                .examId(exam.getExamId())
                .examFullName(exam.getExamFullName())
                .examShortName(exam.getExamShortName())
                .urlDisplayKey(urlDisplayKey)
                .examDescription(exam.getAboutExam())
                .additionalInfo(ExamAdditionalInfoParams)
                .topCoachingInstitutes(this.buildTopCoachingInstituteResponse(
                        coachingInstituteFields, exam))
                .topCoachingCourses(this.buildTopCoachingProgramResponse(
                        coachingProgramFields, coachingInstituteFields, exam))
                .importantDates(this.buildExamImportantDates(exam))
                .build();
    }

    private void getFieldsForEntities(List<String> groupFields, List<String> examFields,
            List<String> coachingInstituteFields, List<String> coachingProgramFields) {
        for (String requestedField : groupFields) {
            if (requestedField.contains(COACHING_INSTITUTE_PREFIX)) {
                coachingInstituteFields.add(requestedField
                        .substring(COACHING_INSTITUTE_PREFIX.length()));
            } else if (requestedField.contains(COACHING_COURSE_PREFIX)) {
                coachingProgramFields.add(requestedField
                        .substring(COACHING_COURSE_PREFIX.length()));
            } else {
                examFields.add(requestedField);
            }
        }
    }

    private List<com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute>
        buildTopCoachingInstituteResponse(
            List<String> coachingInstituteFields, Exam exam) {
        List<com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute>
                topCoachingInstitutesResponse = new ArrayList<>();
        if (CollectionUtils.isEmpty(exam.getTopCoachingInstituteIds())) {
            return topCoachingInstitutesResponse;
        }

        List<CoachingInstituteEntity> coachingInstitutes = this.commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, exam.getTopCoachingInstituteIds(),
                        CoachingInstituteEntity.class, coachingInstituteFields);

        for (CoachingInstituteEntity coachingInstitute : coachingInstitutes) {
            com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute toInsert =
                    com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute
                            .builder().id(coachingInstitute.getInstituteId())
                            .image(coachingInstitute.getLogo())
                            .name(coachingInstitute.getBrandName())
                            .urlDisplayKey(CommonUtils
                                    .convertNameToUrlDisplayName(coachingInstitute.getBrandName()))
                            .build();

            topCoachingInstitutesResponse.add(toInsert);
        }
        return topCoachingInstitutesResponse;
    }

    private List<TopCoachingCoursesForExam> buildTopCoachingProgramResponse(
            List<String> coachingProgramFields, List<String> coachingInstituteFields, Exam exam) {
        List<TopCoachingCoursesForExam> topCoachingProgramsResponse = new ArrayList<>();
        if (CollectionUtils.isEmpty(exam.getTopCoachingCourseIds())) {
            return topCoachingProgramsResponse;
        }

        List<CoachingCourseEntity> coachingProgramEntities = this.commonMongoRepository
                .getEntityFieldsByValuesIn(COACHING_COURSE_ID, exam.getTopCoachingCourseIds(),
                        CoachingCourseEntity.class, coachingProgramFields);

        Set<Long> uniqueCoachingInstituteIds = new HashSet<>();
        for (CoachingCourseEntity coachingProgram : coachingProgramEntities) {
            uniqueCoachingInstituteIds.add(coachingProgram.getCoachingInstituteId());
        }

        List<Long> coachingInstitutesToQuery = new ArrayList<>(uniqueCoachingInstituteIds);
        List<CoachingInstituteEntity> coachingInstitutesForPrograms = this.commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, coachingInstitutesToQuery,
                        CoachingInstituteEntity.class, coachingInstituteFields);

        Map<Long, CoachingInstituteEntity> coachingInstituteMap = new HashMap<>();
        for (CoachingInstituteEntity coachingInstitute : coachingInstitutesForPrograms) {
            coachingInstituteMap.put(coachingInstitute.getInstituteId(), coachingInstitute);
        }

        for (CoachingCourseEntity coachingCourseEntity : coachingProgramEntities) {
            CoachingInstituteEntity coachingInstituteDetails = coachingInstituteMap.getOrDefault(
                    coachingCourseEntity.getCoachingInstituteId(), new CoachingInstituteEntity());

            TopCoachingCoursesForExam toInsert = TopCoachingCoursesForExam.builder()
                    .courseType(coachingCourseEntity.getCourseType())
                    .durationMonths(coachingCourseEntity.getDuration())
                    .eligibility(coachingCourseEntity.getEligibility())
                    .courseId(coachingCourseEntity.getCourseId())
                    .courseName(coachingCourseEntity.getName())
                    .coachingInstituteId(coachingCourseEntity.getCoachingInstituteId())
                    .coachingInstituteName(coachingInstituteDetails.getBrandName())
                    .logo(coachingInstituteDetails.getLogo())
                    .urlDisplayKey(CommonUtils
                            .convertNameToUrlDisplayName(coachingCourseEntity.getName()))
                    .build();

            topCoachingProgramsResponse.add(toInsert);
        }
        return topCoachingProgramsResponse;
    }

    private List<ExamImportantDate> buildExamImportantDates(Exam exam) {
        List<ExamImportantDate> importantDates = new ArrayList<>();
        if (CollectionUtils.isEmpty(exam.getInstances())) {
            return importantDates;
        }

        int relevantInstanceIndex = this.getRelevantInstanceIndex(exam.getInstances());

        importantDates.addAll(this.convertEventEntityToResponse(
                exam.getExamFullName(),
                exam.getInstances().get(relevantInstanceIndex).getEvents()));

        return importantDates;
    }

    private int getRelevantInstanceIndex(List<Instance> instances) {
        int instanceIndex = 0;
        Date presentDate = new Date();
        Date futureMinDate = MAX_DATE;
        Date pastMaxDate = MIN_DATE;

        for (int index = 0; index < instances.size(); index++) {
            Date minApplicationDate = MAX_DATE;
            if (!CollectionUtils.isEmpty(instances.get(index).getEvents())) {
                List<Event> events = instances.get(index).getEvents();

                for (Event event : events) {
                    Date eventDate;
                    if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainty())) {
                        eventDate = event.getDate() != null
                                ? event.getDate()
                                : event.getDateRangeStart();
                    } else {
                        eventDate = stringToDate(event.getMonthDate(), YYYY_MM);
                    }

                    if (eventDate != null && minApplicationDate.after(eventDate)) {
                        minApplicationDate = eventDate;
                    }
                    if (eventDate != null && minApplicationDate.after(presentDate)
                            && futureMinDate.after(eventDate)) {
                        futureMinDate = minApplicationDate;
                        instanceIndex = index;
                    } else if (futureMinDate.equals(MAX_DATE)
                            && minApplicationDate.after(pastMaxDate)) {
                        pastMaxDate = minApplicationDate;
                        instanceIndex = index;
                    }
                }
            }
        }
        return instanceIndex;
    }

    private List<ExamImportantDate> convertEventEntityToResponse(String examName,
            List<Event> events) {
        List<ExamImportantDate> response = new ArrayList<>();

        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                ExamImportantDate toAdd = ExamImportantDate
                        .builder()
                        .name(examName)
                        .monthDate(DateUtil.formatDateString(
                                event.getMonthDate(), YYYY_MM, MMM_YYYY))
                        .monthTimestamp(DateUtil.stringToDate(event.getMonthDate(), YYYY_MM))
                        .modes(event.getModes())
                        .type(event.getType())
                        .typeDisplayName(event.getType())
                        .certainity(event.getCertainty())
                        .build();

                if (event.getDateRangeStart() != null) {
                    toAdd.setDateEndRange(event.getDateRangeEnd());
                    toAdd.setDateStartRange(event.getDateRangeStart());
                    toAdd.setDateEndRangeTimestamp(event.getDateRangeEnd());
                    toAdd.setDateStartRangeTimestamp(event.getDateRangeStart());
                } else {
                    toAdd.setDateStartRangeTimestamp(event.getDate());
                    toAdd.setDateStartRange(event.getDate());
                }
                response.add(toAdd);
            }
        }
        return response;
    }
}
