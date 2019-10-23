package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetExamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.consumer.service.details.helper.CoachingExamSectionHelper;
import com.paytm.digital.education.coaching.consumer.service.search.helper.SearchDataHelper;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamLogoHelper;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ADDITIONAL_INFO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_DETAILS_FIELDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_DETAIL_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.BUTTON_TEXT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.DESCRIPTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.HEADER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SECTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_DETAILS_FIELDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_COURSES_FOR;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_INSTITUTES_FOR;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class ExamService {

    private final CommonMongoRepository     commonMongoRepository;
    private final CoachingCourseService     coachingCourseService;
    private final CoachingInstituteService  coachingInstituteService;
    private final SearchDataHelper          searchDataHelper;
    private final PropertyReader            propertyReader;
    private final CoachingExamSectionHelper coachingExamSectionHelper;
    private final ExamInstanceHelper        examInstanceHelper;
    private final ExamLogoHelper            examLogoHelper;

    public GetExamDetailsResponse getExamDetails(final long examId, final String urlDisplayKey) {
        Exam exam = this.commonMongoRepository.getEntityByFields(EXAM_ID, examId, Exam.class,
                EXAM_DETAILS_FIELDS);
        if (Objects.isNull(exam)) {
            log.error("Exam with id: {} does not exist", examId);
            throw new BadRequestException(INVALID_EXAM_ID);
        }
        if (!CommonUtils.convertNameToUrlDisplayName(exam.getExamFullName())
                .equals(urlDisplayKey)) {
            log.error("Exam with url display key: {} does not exist for exam_id: {}",
                    urlDisplayKey, examId);
            throw new BadRequestException(INVALID_EXAM_NAME);
        }

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections = (List<String>) propertyMap.getOrDefault(EXAM, new ArrayList<>());

        Instance nearestInstance;
        if (examInstanceHelper.getNearestInstance(exam.getInstances()).isPresent()) {
            nearestInstance = examInstanceHelper.getNearestInstance(exam.getInstances()).get();
        } else {
            nearestInstance = new Instance();
        }

        Map<String, Instance> subExamInstances =
                examInstanceHelper.getSubExamInstances(exam, nearestInstance.getInstanceId());

        GetExamDetailsResponse examDetailsResponse = GetExamDetailsResponse.builder()
                .examId(exam.getExamId())
                .examFullName(exam.getExamFullName())
                .examShortName(exam.getExamShortName())
                .logo(examLogoHelper.getExamLogoUrl(exam.getExamId(), exam.getLogo()))
                .urlDisplayKey(urlDisplayKey)
                .examDescription(exam.getAboutExam())
                .topCoachingInstitutes(this.getTopCoachingInstitutes(exam))
                .topCoachingCourses(this.getTopCoachingCourses(exam))
                .sections(sections)
                .importantDates(examInstanceHelper
                        .getImportantDates(exam, nearestInstance, subExamInstances))
                .importantDatesBannerDetails(this.getImportantDatesBannerDetails())
                .build();

        Map<String, Object> sectionConfigurationMap =
                propertyReader
                        .getPropertiesAsMapByKey(DETAILS_PROPERTY_COMPONENT, EXAM_DETAIL_NAMESPACE,
                                SECTION);

        List<String> additionalInfoSections =
                (List<String>) propertyMap.getOrDefault(EXAM_ADDITIONAL_INFO, new ArrayList<>());

        coachingExamSectionHelper
                .addDataPerSection(exam, examDetailsResponse, additionalInfoSections,
                nearestInstance,
                subExamInstances, sectionConfigurationMap, true);
        return examDetailsResponse;
    }

    private TopCoachingCourses getTopCoachingCourses(Exam exam) {
        List<CoachingCourseData> courses = coachingCourseService.getTopCoachingCoursesForExamId(
                exam.getExamId());

        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_COURSE_EXAMS, Collections.singletonList(exam.getExamShortName()));

        if (!CollectionUtils.isEmpty(courses)) {
            return TopCoachingCourses.builder()
                    .header(String.format(TOP_COACHING_COURSES_FOR.getValue(),
                            exam.getExamShortName()))
                    .results(courses)
                    .filter(filter)
                    .build();
        }

        long streamId;
        if (CollectionUtils.isEmpty(exam.getStreamIds())) {
            courses = new ArrayList<>();
        } else {
            streamId = exam.getStreamIds().get(0);
            courses = coachingCourseService.getTopCoachingCoursesForStreamId(streamId);
            StreamEntity streamEntity = this.getStreamEntity(streamId);
            if (Objects.nonNull(streamEntity)) {
                filter.remove(COACHING_COURSE_EXAMS);
                filter.put(COACHING_COURSE_STREAMS,
                        Collections.singletonList(streamEntity.getName()));
            }
        }

        if (CollectionUtils.isEmpty(courses)) {
            courses = new ArrayList<>();
        }

        return TopCoachingCourses.builder()
                .header(TOP_COACHING_COURSES_FOR.getValue())
                .results(courses)
                .filter(filter)
                .build();
    }

    private StreamEntity getStreamEntity(final long streamId) {
        return commonMongoRepository.getEntityByFields(STREAM_ID,
                streamId, StreamEntity.class, STREAM_DETAILS_FIELDS);
    }

    private TopCoachingInstitutes getTopCoachingInstitutes(Exam exam) {
        List<CoachingInstituteData> institutes = coachingInstituteService
                .getTopCoachingInstitutesByExamId(exam.getExamId());

        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_INSTITUTE_EXAMS,
                Collections.singletonList(exam.getExamShortName()));

        if (!CollectionUtils.isEmpty(institutes)) {
            return TopCoachingInstitutes.builder()
                    .header(String.format(TOP_COACHING_INSTITUTES_FOR.getValue(),
                            exam.getExamShortName()))
                    .results(institutes)
                    .filter(filter)
                    .build();
        }

        long streamId;
        if (CollectionUtils.isEmpty(exam.getStreamIds())) {
            institutes = new ArrayList<>();
        } else {
            streamId = exam.getStreamIds().get(0);
            institutes = coachingInstituteService.getTopCoachingInstitutesByStreamId(streamId);
            StreamEntity streamEntity = this.getStreamEntity(streamId);
            if (Objects.nonNull(streamEntity)) {
                filter.remove(COACHING_INSTITUTE_EXAMS);
                filter.put(COACHING_INSTITUTE_STREAMS,
                        Collections.singletonList(streamEntity.getName()));
            }
        }

        if (CollectionUtils.isEmpty(institutes)) {
            institutes = new ArrayList<>();
        }

        return TopCoachingInstitutes.builder()
                .header(TOP_COACHING_INSTITUTES_FOR.getValue())
                .results(institutes)
                .filter(filter)
                .build();
    }

    private ImportantDatesBannerDetails getImportantDatesBannerDetails() {
        return ImportantDatesBannerDetails.builder()
                .header(HEADER)
                .description(DESCRIPTION)
                .logo(LOGO)
                .buttonText(BUTTON_TEXT)
                .build();
    }

    List<ExamData> getTopExamsByStreamId(Long streamId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(STREAM_IDS, Collections.singletonList(streamId));

        return (List<ExamData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.EXAM, null);
    }
}
