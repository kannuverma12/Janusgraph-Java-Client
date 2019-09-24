package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.coaching.consumer.model.response.CoachingCourseTypeResponse;
import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingInstituteDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.service.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingInstituteTransformer;
import com.paytm.digital.education.coaching.enums.CoachingCourseType;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.TopRankerRepository;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.enums.CourseType;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_GLOBAL_PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.elasticsearch.enums.DataSortOrder.ASC;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingInstituteConsumerService {

    private static final List<String>          COACHING_INSTITUTE_FIELDS =
            Arrays.asList("institute_id", "brand_name", "cover_image", "about_institute",
                    "key_highlights", "streams", "exams", "course_types");
    private static final List<String>          EXAM_FIELDS               =
            Arrays.asList("exam_id", "exam_full_name", "exam_short_name", "logo");
    private static final List<String>          STREAM_FIELDS             =
            Arrays.asList("stream_id", "name", "logo");
    private final        CommonMongoRepository commonMongoRepository;
    private final        TopRankerRepository   topRankerRepository;
    private final        SearchDataHelper      searchDataHelper;
    private final        PropertyReader        propertyReader;

    public GetCoachingInstituteDetailsResponse getCoachingInstituteDetails(long instituteId,
            String urlDisplayKey) {
        CoachingInstituteEntity coachingInstituteEntity =
                commonMongoRepository.getEntityByFields(
                        INSTITUTE_ID, instituteId, CoachingInstituteEntity.class,
                        COACHING_INSTITUTE_FIELDS);
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
                getStreamsByStreamIds(coachingInstituteEntity.getStreams(), STREAM_FIELDS);
        List<Exam> examList = getExamsByExamIds(coachingInstituteEntity.getExams(), EXAM_FIELDS);
        List<TopRanker> topRankerList = getTopRankersForInstitute(instituteId);
        List<CoachingCourseTypeResponse> listOfCourseType = new ArrayList<>();
        if (Objects.nonNull(coachingInstituteEntity.getCourseTypes())) {
            for (CourseType courseType : coachingInstituteEntity.getCourseTypes()) {
                listOfCourseType.add(CoachingCourseType
                        .getStaticDataByCourseType(courseType));
            }
        }

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections =
                (List<String>) propertyMap.getOrDefault(INSTITUTE, new ArrayList<>());

        return GetCoachingInstituteDetailsResponse.builder()
                .instituteId(coachingInstituteEntity.getInstituteId())
                .instituteName(coachingInstituteEntity.getBrandName())
                .imageUrl(coachingInstituteEntity.getCoverImage())
                .instituteHighlights(
                        CoachingInstituteTransformer.convertInstituteHighlights(
                                coachingInstituteEntity.getKeyHighlights()))
                .streams(streamList)
                .exams(examList)
                .topRankers(topRankerList)
                .coachingCourseTypes(listOfCourseType)
                .sections(sections)
                .build();

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

    public List<CoachingInstituteData> getTopCoachingInstitutesByExamId(Long examId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(EXAM_IDS, Arrays.asList(examId));

        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(IGNORE_GLOBAL_PRIORITY, ASC);

        return (List<CoachingInstituteData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.COACHING_INSTITUTE, sortOrder);
    }

    public List<CoachingInstituteData> getTopCoachingInstitutesByStreamId(Long streamId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(STREAM_IDS, Arrays.asList(streamId));

        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(IGNORE_GLOBAL_PRIORITY, ASC);

        return (List<CoachingInstituteData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.COACHING_INSTITUTE, sortOrder);
    }
}
