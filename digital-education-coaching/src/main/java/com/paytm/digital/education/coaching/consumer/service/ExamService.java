package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.response.GetExamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.consumer.service.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.service.utils.CommonServiceUtils;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ADDITIONAL_INFO_PARAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_DETAILS_FIELDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.BUTTON_TEXT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.DESCRIPTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.HEADER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class ExamService {

    private final CommonMongoRepository            commonMongoRepository;
    private final CoachingCourseService            coachingCourseService;
    private final CoachingInstituteConsumerService coachingInstituteService;
    private final SearchDataHelper                 searchDataHelper;
    private final PropertyReader                   propertyReader;

    public GetExamDetailsResponse getExamDetails(final Long examId, final String urlDisplayKey) {
        Exam exam = this.commonMongoRepository.getEntityByFields(
                EXAM_ID, examId, Exam.class, EXAM_DETAILS_FIELDS);
        if (Objects.isNull(exam)) {
            log.error("Exam with id: {} does not exist", examId);
            throw new BadRequestException(INVALID_EXAM_ID);
        }
        if (!CommonUtils.convertNameToUrlDisplayName(exam.getExamFullName())
                .equals(urlDisplayKey)) {
            log.error("Exam with url display key: {} does not exist for exam_id: {}", urlDisplayKey,
                    examId);
            throw new BadRequestException(INVALID_EXAM_NAME);
        }

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections = (List<String>) propertyMap.getOrDefault(EXAM, new ArrayList<>());

        return GetExamDetailsResponse.builder()
                .examId(exam.getExamId())
                .examFullName(exam.getExamFullName())
                .examShortName(exam.getExamShortName())
                .urlDisplayKey(urlDisplayKey)
                .examDescription(exam.getAboutExam())
                .additionalInfo(this.getExamAdditionalInfo(exam))
                .topCoachingInstitutes(this.getTopCoachingInstitutes(exam))
                .topCoachingCourses(this.getTopCoachingCourses(exam))
                .importantDates(CommonServiceUtils.buildExamImportantDates(exam))
                .sections(sections)
                .importantDatesBannerDetails(this.getImportantDatesBannerDetails())
                .build();
    }

    private TopCoachingCourses getTopCoachingCourses(Exam exam) {
        List<CoachingCourseData> courses = coachingCourseService
                .getTopCoachingCoursesForExamId(exam.getExamId());

        if (!CollectionUtils.isEmpty(courses)) {
            return TopCoachingCourses
                    .builder()
                    .header("Top Coaching Courses for " + exam.getExamShortName())
                    .results(courses)
                    .build();
        }

        if (CollectionUtils.isEmpty(exam.getStreamIds())) {
            courses = new ArrayList<>();
        } else {
            courses = coachingCourseService
                    .getTopCoachingCoursesForStreamId(exam.getStreamIds().get(0));
        }

        if (CollectionUtils.isEmpty(courses)) {
            courses = new ArrayList<>();
        }

        return TopCoachingCourses
                .builder()
                .header("Similar Coaching Courses")
                .results(courses)
                .build();
    }

    private TopCoachingInstitutes getTopCoachingInstitutes(Exam exam) {
        List<CoachingInstituteData> institutes = coachingInstituteService
                .getTopCoachingInstitutesByExamId(exam.getExamId());

        if (!CollectionUtils.isEmpty(institutes)) {
            return TopCoachingInstitutes
                    .builder()
                    .header("Top Coaching Institutes for " + exam.getExamShortName())
                    .results(institutes)
                    .build();
        }

        if (CollectionUtils.isEmpty(exam.getStreamIds())) {
            institutes = new ArrayList<>();
        } else {
            institutes = coachingInstituteService
                    .getTopCoachingInstitutesByStreamId(exam.getStreamIds().get(0));
        }

        if (CollectionUtils.isEmpty(institutes)) {
            institutes = new ArrayList<>();
        }

        return TopCoachingInstitutes
                .builder()
                .header("Similar Coaching Institutes")
                .results(institutes)
                .build();
    }

    private ExamAdditionalInfo getExamAdditionalInfo(Exam exam) {
        return ExamAdditionalInfo
                .builder()
                .header("All you need to know about " + exam.getExamShortName())
                .results(EXAM_ADDITIONAL_INFO_PARAMS)
                .build();
    }

    private ImportantDatesBannerDetails getImportantDatesBannerDetails() {
        return ImportantDatesBannerDetails
                .builder()
                .header(HEADER)
                .description(DESCRIPTION)
                .logo(LOGO)
                .buttonText(BUTTON_TEXT)
                .build();
    }

    public List<ExamData> getTopExamsbyStreamId(Long streamId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(STREAM_IDS, Arrays.asList(streamId));

        return (List<ExamData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.EXAM, null);
    }
}
