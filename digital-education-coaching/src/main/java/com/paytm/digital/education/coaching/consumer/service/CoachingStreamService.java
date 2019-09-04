package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.response.GetStreamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_DETAILS_FIELDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingStreamService {

    private final CommonMongoRepository            commonMongoRepository;
    private final CoachingCourseService            coachingCourseService;
    private final CoachingInstituteConsumerService coachingInstituteService;
    private final ExamService                      examService;

    public GetStreamDetailsResponse getStreamDetails(final long streamId,
            final String urlDisplayKey) {
        StreamEntity streamEntity = commonMongoRepository.getEntityByFields(
                STREAM_ID, streamId, StreamEntity.class, STREAM_DETAILS_FIELDS);

        if (Objects.isNull(streamEntity)) {
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

        List<ExamImportantDate> examImportantDates = new ArrayList<>();
        List<ExamData> topExams = this.getTopExamsForStream(streamEntity);

        return GetStreamDetailsResponse.builder()
                .streamId(streamEntity.getStreamId())
                .streamName(streamEntity.getName())
                .topExams(topExams)
                .examImportantDates(examImportantDates)
                .topCoachingInstitutes(this.getTopCoachingInstitutesForStream(streamEntity))
                .topCoachingCourses(this.getTopCoachingCoursesForStream(streamEntity))
                .build();
    }

    private TopCoachingCourses getTopCoachingCoursesForStream(StreamEntity stream) {
        List<CoachingCourseData> courses = coachingCourseService
                .getTopCoachingCoursesForStreamId(stream.getStreamId());

        return TopCoachingCourses
                .builder()
                .header("Top Coaching Courses for " + stream.getName())
                .results(courses)
                .build();
    }

    private TopCoachingInstitutes getTopCoachingInstitutesForStream(StreamEntity stream) {
        List<CoachingInstituteData> institutes = coachingInstituteService
                .getTopCoachingInstitutesByStreamId(stream.getStreamId());

        return TopCoachingInstitutes
                .builder()
                .header("Top Coaching Institutes for " + stream.getName())
                .results(institutes)
                .build();
    }

    private List<ExamData> getTopExamsForStream(StreamEntity stream) {
        return examService.getTopExamsbyStreamId(stream.getStreamId());
    }

}
