package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.dto.TopExams;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetStreamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingStreamService {

    private final CommonMongoRepository    commonMongoRepository;
    private final CoachingCourseService    coachingCourseService;
    private final CoachingInstituteService coachingInstituteService;
    private final ExamService              examService;
    private final PropertyReader           propertyReader;

    public GetStreamDetailsResponse getStreamDetails(final long streamId,
            final String urlDisplayKey) {
        StreamEntity streamEntity = commonMongoRepository.getEntityByFields(
                STREAM_ID, streamId, StreamEntity.class, STREAM_DETAILS_FIELDS);

        if (Objects.isNull(streamEntity) || !streamEntity.getIsEnabled()) {
            log.error("stream with id: {} does not exist", streamId);
            throw new BadRequestException(INVALID_STREAM_ID);
        }
        if (!CommonUtils.convertNameToUrlDisplayName(streamEntity.getName())
                .equals(urlDisplayKey)) {
            log.error("Stream with url display key: {} does not exist for stream_id: {}",
                    urlDisplayKey,
                    streamId);
            throw new BadRequestException(INVALID_STREAM_NAME);
        }

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections = (List<String>) propertyMap.getOrDefault(STREAM, new ArrayList<>());

        return GetStreamDetailsResponse.builder()
                .streamId(streamEntity.getStreamId())
                .streamName(WordUtils.capitalizeFully(streamEntity.getName()))
                .topExams(this.getTopExamsForStream(streamEntity))
                .topCoachingInstitutes(this.getTopCoachingInstitutesForStream(streamEntity))
                .topCoachingCourses(this.getTopCoachingCoursesForStream(streamEntity))
                .sections(sections)
                .importantDatesBannerDetails(this.getImportantDatesBannerDetails())
                .build();
    }

    private TopCoachingCourses getTopCoachingCoursesForStream(StreamEntity stream) {
        List<CoachingCourseData> courses = coachingCourseService
                .getTopCoachingCoursesForStreamId(stream.getStreamId());

        if (CollectionUtils.isEmpty(courses)) {
            courses = new ArrayList<>();
        }

        return TopCoachingCourses
                .builder()
                .header("Top Coaching Courses for " + WordUtils.capitalizeFully(stream.getName()))
                .results(courses)
                .build();
    }

    private TopCoachingInstitutes getTopCoachingInstitutesForStream(StreamEntity stream) {
        List<CoachingInstituteData> institutes = coachingInstituteService
                .getTopCoachingInstitutesByStreamId(stream.getStreamId());

        if (CollectionUtils.isEmpty(institutes)) {
            institutes = new ArrayList<>();
        }

        return TopCoachingInstitutes
                .builder()
                .header("Top Coaching Institutes for " + WordUtils
                        .capitalizeFully(stream.getName()))
                .results(institutes)
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

    private TopExams getTopExamsForStream(StreamEntity stream) {
        List<ExamData> topExams = examService.getTopExamsbyStreamId(stream.getStreamId());

        if (CollectionUtils.isEmpty(topExams)) {
            topExams = new ArrayList<>();
        }

        return TopExams
                .builder()
                .header("Coaching for " + WordUtils.capitalizeFully(stream.getName()) + " Exams")
                .results(topExams)
                .build();
    }

}
