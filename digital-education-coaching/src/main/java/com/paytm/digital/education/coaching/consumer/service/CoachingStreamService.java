package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourse;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.response.GetStreamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.service.utils.CommonServiceUtils;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingInstituteTransformer;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingStreamTransformer;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_STREAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_EXAM_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_FIELD_GROUP;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_DOMAIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_NAME;

@Slf4j
@Service
public class CoachingStreamService {

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    public GetStreamDetailsResponse getStreamDetails(long streamId, String urlDisplayKey) {

        List<String> groupFields = this.commonMongoRepository.getFieldsByGroup(
                StreamEntity.class, DETAILS_FIELD_GROUP);
        if (CollectionUtils.isEmpty(groupFields)) {
            log.error("Group fields not found for entity: coaching stream and group: {}",
                    DETAILS_FIELD_GROUP);
            throw new BadRequestException(INVALID_FIELD_GROUP);
        }
        List<String> coachingInstituteFields = new ArrayList<>();
        List<String> examFields = new ArrayList<>();
        List<String> streamFields = new ArrayList<>();
        List<String> coachingCourseFields = new ArrayList<>();

        getFieldsForEntities(groupFields, coachingInstituteFields, examFields, streamFields,
                coachingCourseFields);

        StreamEntity streamEntity = commonMongoRepository.getEntityByFields(
                STREAM_ID, streamId, StreamEntity.class,
                streamFields);

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
        List<Exam> topExams =
                getExamsByStreamName(streamEntity.getName(), examFields, examImportantDates);
        List<CoachingInstitute> topCoachingInstitutes =
                getCoachingInstituteByStreamId(streamEntity.getStreamId(),
                        coachingInstituteFields);
        List<CoachingCourse> topCoachingCourses =
                getCoachingCoursesByStreamId(streamEntity.getStreamId(),
                        coachingCourseFields);

        return GetStreamDetailsResponse.builder()
                .streamId(streamEntity.getStreamId())
                .streamName(streamEntity.getName())
                .topExams(topExams)
                .examImportantDates(examImportantDates)
                .topCoachingInstitutes(topCoachingInstitutes)
                .topCoachingCourses(topCoachingCourses)
                .build();
    }

    private List<CoachingCourse> getCoachingCoursesByStreamId(Long streamId,
            List<String> coachingCourseFields) {
        List<CoachingCourse> coachingCourseList = new ArrayList<>();
        if (Objects.isNull(streamId)) {
            return coachingCourseList;
        }
        //To Do This list will come from elastic search
        return coachingCourseList;
    }

    private List<CoachingInstitute> getCoachingInstituteByStreamId(long streamId,
            List<String> coachingInstituteFields) {
        List<CoachingInstitute> coachingInstituteList = new ArrayList<>();
        if (Objects.isNull(streamId)) {
            return coachingInstituteList;
        }
        //To Do This list will come from elastic search
        return coachingInstituteList;
    }

    private List<Exam> getExamsByStreamName(String streamName, List<String> examFields,
            List<ExamImportantDate> examImportantDates) {
        List<Exam> examList = new ArrayList<>();
        if (Objects.isNull(streamName)) {
            return examList;
        }
        //To Do This list will come from elastic search
        return examList;
    }

    private void getFieldsForEntities(List<String> groupFields,
            List<String> coachingInstituteFields,
            List<String> examFields, List<String> streamFields,
            List<String> coachingCourseFields) {
        for (String requestedField : groupFields) {
            if (requestedField.contains(EXAM_PREFIX)) {
                examFields.add(requestedField
                        .substring(EXAM_PREFIX.length()));
            } else if (requestedField.contains(COACHING_INSTITUTE_PREFIX)) {
                coachingInstituteFields.add(requestedField
                        .substring(COACHING_INSTITUTE_PREFIX.length()));
            } else if (requestedField.contains(COACHING_COURSE_PREFIX)) {
                coachingCourseFields.add(requestedField
                        .substring(COACHING_COURSE_PREFIX.length()));
            } else {
                streamFields.add(requestedField);
            }
        }
    }

}
