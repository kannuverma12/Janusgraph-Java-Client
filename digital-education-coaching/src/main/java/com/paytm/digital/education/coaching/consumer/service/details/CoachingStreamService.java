package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.dto.TopExams;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetStreamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.database.dao.CoachingStreamDAO;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
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

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_EXAM_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.BUTTON_TEXT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.DESCRIPTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.HEADER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.ImportantDates.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_DETAILS_FIELDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.COACHING_FOR;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_COURSES_FOR;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_INSTITUTES_FOR;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingStreamService {

    private final CoachingCourseService    coachingCourseService;
    private final CoachingInstituteService coachingInstituteService;
    private final ExamService              examService;
    private final PropertyReader           propertyReader;
    private final CoachingStreamDAO        coachingStreamDAO;

    public GetStreamDetailsResponse getStreamDetails(final long streamId,
            final String urlDisplayKey) {
        StreamEntity streamEntity =
                coachingStreamDAO.findByStreamId(STREAM_ID, streamId, STREAM_DETAILS_FIELDS);

        if (Objects.isNull(streamEntity) || !streamEntity.getIsEnabled()) {
            log.error("Stream with id: {} does not exist", streamId);
            throw new BadRequestException(INVALID_STREAM_ID);
        }
        if (!CommonUtils.convertNameToUrlDisplayName(streamEntity.getName())
                .equals(urlDisplayKey)) {
            log.error("Stream with url display key: {} does not exist for stream_id: {}",
                    urlDisplayKey, streamId);
            throw new BadRequestException(INVALID_STREAM_NAME);
        }

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections = (List<String>) propertyMap.getOrDefault(STREAM, new ArrayList<>());

        return GetStreamDetailsResponse.builder()
                .streamId(streamEntity.getStreamId())
                .streamName(streamEntity.getName())
                .topExams(this.getTopExamsForStream(streamEntity))
                .topCoachingInstitutes(this.getTopCoachingInstitutesForStream(streamEntity))
                .topCoachingCourses(this.getTopCoachingCoursesForStream(streamEntity))
                .sections(sections)
                .importantDatesBannerDetails(this.getImportantDatesBannerDetails())
                .build();
    }

    private TopCoachingCourses getTopCoachingCoursesForStream(StreamEntity stream) {
        List<CoachingCourseData> courses = coachingCourseService.getTopCoachingCoursesForStreamId(
                stream.getStreamId());

        if (CollectionUtils.isEmpty(courses)) {
            courses = new ArrayList<>();
        }

        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_COURSE_STREAMS, Collections.singletonList(stream.getName()));

        return TopCoachingCourses.builder()
                .header(TOP_COACHING_COURSES_FOR.getValue())
                .results(courses)
                .filter(filter)
                .build();
    }

    private TopCoachingInstitutes getTopCoachingInstitutesForStream(StreamEntity stream) {
        List<CoachingInstituteData> institutes = coachingInstituteService
                .getTopCoachingInstitutesByStreamId(stream.getStreamId());

        if (CollectionUtils.isEmpty(institutes)) {
            institutes = new ArrayList<>();
        }

        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_INSTITUTE_STREAMS, Collections.singletonList(stream.getName()));

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

    private TopExams getTopExamsForStream(StreamEntity stream) {
        List<ExamData> topExams = examService.getTopExamsByStreamId(stream.getStreamId());

        if (CollectionUtils.isEmpty(topExams)) {
            topExams = new ArrayList<>();
        }

        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_EXAM_STREAMS, Collections.singletonList(stream.getName()));

        return TopExams.builder()
                .header(String.format(COACHING_FOR.getValue(), stream.getName()))
                .results(topExams)
                .filter(filter)
                .build();
    }
}
