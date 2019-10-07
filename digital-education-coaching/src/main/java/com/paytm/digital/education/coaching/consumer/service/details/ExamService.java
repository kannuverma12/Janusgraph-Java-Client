package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetExamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.details.SectionDataHolder;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.consumer.service.details.helper.ExamSectionHelper;
import com.paytm.digital.education.coaching.consumer.service.search.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.service.utils.CommonServiceUtils;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_LEVEL;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_TYPE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ADDITIONAL_INFO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ADDITIONAL_INFO_PARAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_DETAILS_FIELDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_DETAIL_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.BUTTON_TEXT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.DESCRIPTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.HEADER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SECTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.ALL_YOU_NEED_TO_KNOW_ABOUT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.SIMILAR_COACHING_COURSES;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.SIMILAR_COACHING_INSTITUTES;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_COURSES_FOR;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_INSTITUTES_FOR;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class ExamService {

    private final CommonMongoRepository    commonMongoRepository;
    private final CoachingCourseService    coachingCourseService;
    private final CoachingInstituteService coachingInstituteService;
    private final SearchDataHelper         searchDataHelper;
    private final PropertyReader           propertyReader;
    private final ExamSectionHelper        examSectionHelper;
    private final ExamInstanceHelper       examInstanceHelper;

    private static final List<String> FILTERS_APPLICABLE =
            Collections.singletonList(COACHING_COURSE_EXAMS);

    public GetExamDetailsResponse getExamDetails(final Long examId, final String urlDisplayKey) {
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

        Instance nearestInstance = examInstanceHelper.getNearestInstance(exam.getInstances()).get();

        Map<String, Instance> subExamInstances =
                examInstanceHelper.getSubExamInstances(exam, nearestInstance.getInstanceId());

        GetExamDetailsResponse examDetailsResponse = GetExamDetailsResponse.builder()
                .examId(exam.getExamId())
                .examFullName(exam.getExamFullName())
                .examShortName(exam.getExamShortName())
                .urlDisplayKey(urlDisplayKey)
                .examDescription(exam.getAboutExam())
                .additionalInfo(this.getExamAdditionalInfo(exam))
                .topCoachingInstitutes(this.getTopCoachingInstitutes(exam))
                .topCoachingCourses(this.getTopCoachingCourses(exam))
                .importantDates(examInstanceHelper
                        .getImportantDates(exam, nearestInstance, subExamInstances))
                .sections(sections)
                .importantDatesBannerDetails(this.getImportantDatesBannerDetails())
                .filters(FILTERS_APPLICABLE)
                .build();

        Map<String, Object> sectionConfigurationMap =
                propertyReader
                        .getPropertiesAsMapByKey(DETAILS_PROPERTY_COMPONENT, EXAM_DETAIL_NAMESPACE,
                                SECTION);

        List<String> additionalInfoSections =
                (List<String>) propertyMap.getOrDefault(EXAM_ADDITIONAL_INFO, new ArrayList<>());

        examSectionHelper.addDataPerSection(exam, examDetailsResponse, additionalInfoSections,
                nearestInstance,
                subExamInstances, sectionConfigurationMap, true);

        return examDetailsResponse;
    }

    private TopCoachingCourses getTopCoachingCourses(Exam exam) {
        List<CoachingCourseData> courses = coachingCourseService.getTopCoachingCoursesForExamId(
                exam.getExamId());

        if (!CollectionUtils.isEmpty(courses)) {
            return TopCoachingCourses.builder()
                    .header(String.format(TOP_COACHING_COURSES_FOR.getValue(),
                            exam.getExamShortName()))
                    .results(courses)
                    .build();
        }

        if (CollectionUtils.isEmpty(exam.getStreamIds())) {
            courses = new ArrayList<>();
        } else {
            courses = coachingCourseService.getTopCoachingCoursesForStreamId(
                    exam.getStreamIds().get(0));
        }

        if (CollectionUtils.isEmpty(courses)) {
            courses = new ArrayList<>();
        }

        return TopCoachingCourses.builder()
                .header(SIMILAR_COACHING_COURSES.getValue())
                .results(courses)
                .build();
    }

    private TopCoachingInstitutes getTopCoachingInstitutes(Exam exam) {
        List<CoachingInstituteData> institutes = coachingInstituteService
                .getTopCoachingInstitutesByExamId(exam.getExamId());

        if (!CollectionUtils.isEmpty(institutes)) {
            return TopCoachingInstitutes.builder()
                    .header(String.format(TOP_COACHING_INSTITUTES_FOR.getValue(),
                            exam.getExamShortName()))
                    .results(institutes)
                    .build();
        }

        if (CollectionUtils.isEmpty(exam.getStreamIds())) {
            institutes = new ArrayList<>();
        } else {
            institutes = coachingInstituteService.getTopCoachingInstitutesByStreamId(
                    exam.getStreamIds().get(0));
        }

        if (CollectionUtils.isEmpty(institutes)) {
            institutes = new ArrayList<>();
        }

        return TopCoachingInstitutes.builder()
                .header(SIMILAR_COACHING_INSTITUTES.getValue())
                .results(institutes)
                .build();
    }

    private ExamAdditionalInfo getExamAdditionalInfo(Exam exam) {
        return ExamAdditionalInfo.builder()
                .header(String.format(ALL_YOU_NEED_TO_KNOW_ABOUT.getValue(),
                        exam.getExamShortName()))
                .results(EXAM_ADDITIONAL_INFO_PARAMS)
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
