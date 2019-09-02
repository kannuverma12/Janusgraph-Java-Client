package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingInstituteDetailsResponse;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingInstituteTransformer;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.TopRankerRepository;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_STREAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_FIELD_GROUP;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;

@Slf4j
@Service
public class CoachingInstituteConsumerService {

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    @Autowired
    private TopRankerRepository topRankerRepository;

    public GetCoachingInstituteDetailsResponse getCoachingInstituteDetails(long instituteId,
            String urlDisplayKey) {
        List<String> groupFields = this.commonMongoRepository.getFieldsByGroup(
                CoachingInstituteEntity.class, DETAILS_FIELD_GROUP);
        if (CollectionUtils.isEmpty(groupFields)) {
            log.error("Group fields not found for entity: coaching institute and group: {}",
                    DETAILS_FIELD_GROUP);
            throw new BadRequestException(INVALID_FIELD_GROUP);
        } else {
            List<String> coachingInstituteFields = new ArrayList<>();
            List<String> examFields = new ArrayList<>();
            List<String> streamFields = new ArrayList<>();
            getFieldsForEntities(groupFields, coachingInstituteFields, examFields, streamFields);

            CoachingInstituteEntity coachingInstituteEntity =
                    commonMongoRepository.getEntityByFields(
                            INSTITUTE_ID, instituteId, CoachingInstituteEntity.class,
                            coachingInstituteFields);
            if (Objects.isNull(coachingInstituteEntity)) {
                log.error("Institute with id: {} does not exist", instituteId);
                throw new BadRequestException(INVALID_INSTITUTE_ID);
            }
            if (!CommonUtils.convertNameToUrlDisplayName(coachingInstituteEntity.getBrandName())
                    .equals(urlDisplayKey)) {
                log.error("Institute with url display key: {} does not exist for institute_id: {}",
                        urlDisplayKey,
                        instituteId);
                throw new BadRequestException(INVALID_INSTITUTE_NAME);
            }

            List<Stream> streamList =
                    getStreamsByStreamIds(coachingInstituteEntity.getStreams(), streamFields);
            List<Exam> examList = getExamsByExamIds(coachingInstituteEntity.getExams(), examFields);
            List<TopRanker> topRankerList = getTopRankersForInstitute(instituteId);
            List<String> listOfCourseType = null;
            if (Objects.nonNull(coachingInstituteEntity.getCourseTypes())) {
                listOfCourseType =
                        coachingInstituteEntity.getCourseTypes().stream().map(
                                CourseType::getText).collect(Collectors.toList());
            }

            return GetCoachingInstituteDetailsResponse.builder()
                    .instituteId(coachingInstituteEntity.getInstituteId())
                    .instituteName(coachingInstituteEntity.getBrandName())
                    .imageUrl(coachingInstituteEntity.getCoverImage())
                    .instituteHighLights(
                            CoachingInstituteTransformer.convertInstituteHighlights(
                                    coachingInstituteEntity.getKeyHighlights()))
                    .streams(streamList)
                    .exams(examList)
                    .topRankers(topRankerList)
                    .coachingCourseTypes(listOfCourseType)
                    .build();
        }
    }

    private void getFieldsForEntities(List<String> groupFields,
            List<String> coachingInstituteFields,
            List<String> examFields, List<String> streamFields) {
        for (String requestedField : groupFields) {
            if (requestedField.contains(EXAM_PREFIX)) {
                examFields.add(requestedField
                        .substring(EXAM_PREFIX.length()));
            } else if (requestedField.contains(COACHING_STREAM_PREFIX)) {
                streamFields.add(requestedField
                        .substring(COACHING_STREAM_PREFIX.length()));
            } else {
                coachingInstituteFields.add(requestedField);
            }
        }
    }

    private List<Exam> getExamsByExamIds(List<Long> examIds, List<String> examFields) {
        List<Exam> examList = new ArrayList<>();
        if (CollectionUtils.isEmpty(examIds)) {
            return examList;
        }
        List<com.paytm.digital.education.database.entity.Exam> examEntityList =
                commonMongoRepository.getEntityFieldsByValuesIn(EXAM_ID, examIds,
                        com.paytm.digital.education.database.entity.Exam.class, examFields);
        return CoachingInstituteTransformer.convertExamEntityToDto(examList, examEntityList);
    }

    private List<Stream> getStreamsByStreamIds(List<Long> streamIds, List<String> streamFields) {
        List<Stream> streamList = new ArrayList<>();
        if (CollectionUtils.isEmpty(streamIds)) {
            return streamList;
        }
        List<com.paytm.digital.education.database.entity.StreamEntity> streamEntityList =
                commonMongoRepository.getEntityFieldsByValuesIn(STREAM_ID, streamIds,
                        StreamEntity.class, streamFields);
        return CoachingInstituteTransformer
                .convertStreamEntityToStreamDto(streamList, streamEntityList);
    }

    private List<TopRanker> getTopRankersForInstitute(long instituteId) {
        Set<Long> examIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();
        Map<Long, String> examIdsAndNameMap;
        Map<Long, String> coachingCourseIdsAndNameMap;
        List<TopRankerEntity> topRankerEntityList =
                topRankerRepository.findByInstituteId(instituteId);
        final List<TopRanker> topRankerList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(topRankerEntityList)) {
            for (TopRankerEntity topRankerEntity : topRankerEntityList) {
                if (Objects.nonNull(topRankerEntity.getExamId())) {
                    examIds.add(topRankerEntity.getExamId());
                }
                if (Objects.nonNull(topRankerEntity.getCourseIds())) {
                    courseIds.addAll(topRankerEntity.getCourseIds());
                }
            }
        }
        examIdsAndNameMap = getExamIdsToNameMap((new ArrayList<>(examIds)));
        coachingCourseIdsAndNameMap =
                getCoachingCourseIdsToNameMap((new ArrayList<>(courseIds)));
        return CoachingInstituteTransformer.convertTopRankerEntityToTopRankerDto(examIdsAndNameMap,
                coachingCourseIdsAndNameMap,
                topRankerEntityList, topRankerList);
    }

    private Map<Long, String> getExamIdsToNameMap(List<Long> examIds) {
        Map<Long, String> examIdsAndNameMap = new HashMap<>();
        if (CollectionUtils.isEmpty(examIds)) {
            return examIdsAndNameMap;
        }
        List<String> examFields = new ArrayList<String>() {
            {
                add("exam_id");
                add("exam_short_name");
            }
        };
        List<com.paytm.digital.education.database.entity.Exam> examEntityList =
                commonMongoRepository.getEntityFieldsByValuesIn(EXAM_ID, examIds,
                        com.paytm.digital.education.database.entity.Exam.class, examFields);
        if (!CollectionUtils.isEmpty(examEntityList)) {
            for (com.paytm.digital.education.database.entity.Exam examEntity : examEntityList) {
                examIdsAndNameMap.put(examEntity.getExamId(), examEntity.getExamShortName());
            }
        }
        return examIdsAndNameMap;
    }

    private Map<Long, String> getCoachingCourseIdsToNameMap(List<Long> courseIds) {
        Map<Long, String> coachingCourseIdsAndNameMap = new HashMap<>();
        if (CollectionUtils.isEmpty(courseIds)) {
            return coachingCourseIdsAndNameMap;
        }
        List<String> coachingCourseFields = new ArrayList<String>() {
            {
                add("course_id");
                add("name");
            }
        };
        List<CoachingCourseEntity> coachingCourseEntityList =
                commonMongoRepository.getEntityFieldsByValuesIn(COURSE_ID, courseIds,
                        CoachingCourseEntity.class, coachingCourseFields);
        if (!CollectionUtils.isEmpty(coachingCourseEntityList)) {
            for (CoachingCourseEntity coachingCourseEntity : coachingCourseEntityList) {
                coachingCourseIdsAndNameMap.put(coachingCourseEntity.getCourseId(),
                        coachingCourseEntity.getName());
            }
        }
        return coachingCourseIdsAndNameMap;
    }
}
