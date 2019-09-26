package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourseTypeInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.Faq;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopExamsInstitute;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRankers;
import com.paytm.digital.education.coaching.consumer.model.dto.TopStreams;
import com.paytm.digital.education.coaching.consumer.model.response.details.CoachingCourseTypeResponse;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetCoachingInstituteDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.service.search.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingInstituteTransformer;
import com.paytm.digital.education.coaching.enums.CoachingCourseType;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mongodb.QueryOperators.AND;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_COVER_IMAGE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_GLOBAL_PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_IMAGE;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_LOGO;
import static com.paytm.digital.education.elasticsearch.enums.DataSortOrder.ASC;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingInstituteService {

    private static final List<String> COACHING_INSTITUTE_FIELDS =
            Arrays.asList("institute_id", "brand_name", "cover_image", "about_institute",
                    "key_highlights", "streams", "exams", "course_types", "faqs",
                    "more_info1", "more_info2", "more_info3", "more_info4", "logo");

    private static final List<String> EXAM_FIELDS =
            Arrays.asList("exam_id", "exam_full_name", "exam_short_name", "logo");

    private static final List<String> STREAM_FIELDS =
            Arrays.asList("stream_id", "name", "logo");

    private final CommonMongoRepository commonMongoRepository;
    private final SearchDataHelper      searchDataHelper;
    private final PropertyReader        propertyReader;

    public GetCoachingInstituteDetailsResponse getCoachingInstituteDetails(long instituteId,
            String urlDisplayKey, Long streamId, Long examId) {
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

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections =
                (List<String>) propertyMap.getOrDefault(INSTITUTE, new ArrayList<>());

        return GetCoachingInstituteDetailsResponse.builder()
                .instituteId(coachingInstituteEntity.getInstituteId())
                .instituteName(coachingInstituteEntity.getBrandName())
                .description(coachingInstituteEntity.getAboutInstitute())
                .imageUrl(ImageUtils
                        .getImageWithAbsolutePath(coachingInstituteEntity.getCoverImage(),
                                INSTITUTE_COVER_IMAGE_PLACEHOLDER, TOP_COACHING_INSTITUTES_IMAGE))
                .logo(ImageUtils.getImageWithAbsolutePath(coachingInstituteEntity.getLogo(),
                        INSTITUTE_PLACEHOLDER, TOP_COACHING_INSTITUTES_LOGO))
                .instituteHighlights(
                        CoachingInstituteTransformer.convertInstituteHighlights(
                                coachingInstituteEntity.getKeyHighlights()))
                .streams(this.getStreamsForInstitute(coachingInstituteEntity))
                .exams(this.getExamsForInstitute(coachingInstituteEntity))
                .topRankers(this.getTopRankersForInstitute(instituteId, streamId, examId))
                .coachingCourseTypes(this.getCoachingCourseTypes(coachingInstituteEntity))
                .topCoachingCourses(
                        this.getTopCoachingCoursesForInstitute(coachingInstituteEntity, streamId,
                                examId))
                .faqs(this.fillFaqs(coachingInstituteEntity.getFaqs()))
                .sections(sections)
                .build();
    }

    private CoachingCourseTypeInfo getCoachingCourseTypes(
            CoachingInstituteEntity coachingInstituteEntity) {
        List<CoachingCourseTypeResponse> listOfCourseType = new ArrayList<>();
        if (Objects.nonNull(coachingInstituteEntity.getCourseTypes())) {
            for (CourseType courseType : coachingInstituteEntity.getCourseTypes()) {
                listOfCourseType.add(CoachingCourseType
                        .getStaticDataByCourseType(courseType));
            }
        }

        return CoachingCourseTypeInfo
                .builder()
                .header("Browse by Course Type")
                .results(listOfCourseType)
                .build();
    }

    private TopCoachingCourses getTopCoachingCoursesForInstitute(
            CoachingInstituteEntity coachingInstituteEntity, Long streamId, Long examId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_INSTITUTE_ID,
                Collections.singletonList(coachingInstituteEntity.getInstituteId()));

        if (Objects.nonNull(examId)) {
            filter.put(EXAM_IDS, Collections.singletonList(examId));
        } else if (Objects.nonNull(streamId)) {
            filter.put(STREAM_IDS, Collections.singletonList(streamId));
        }

        List<CoachingCourseData> topCoachingCourses =
                (List<CoachingCourseData>) (List<?>) searchDataHelper
                        .getTopSearchData(filter, EducationEntity.COACHING_COURSE, null);

        if (CollectionUtils.isEmpty(topCoachingCourses)) {
            topCoachingCourses = new ArrayList<>();
        }

        return TopCoachingCourses
                .builder()
                .header("Top Coaching Courses by " + coachingInstituteEntity.getBrandName())
                .results(topCoachingCourses)
                .build();
    }

    private List<Faq> fillFaqs(final List<com.paytm.digital.education.database.embedded.Faq> faqs) {
        if (CollectionUtils.isEmpty(faqs)) {
            return Collections.EMPTY_LIST;
        }
        return faqs.stream()
                .map(faq -> Faq.builder()
                        .question(faq.getQuestion())
                        .answer(faq.getAnswers())
                        .build())
                .collect(Collectors.toList());
    }

    private TopExamsInstitute getExamsForInstitute(
            CoachingInstituteEntity coachingInstituteEntity) {
        List<Exam> examList = new ArrayList<>();
        if (CollectionUtils.isEmpty(coachingInstituteEntity.getExams())) {
            return TopExamsInstitute
                    .builder()
                    .header("Top Exams prepared for by " + coachingInstituteEntity.getBrandName())
                    .results(examList)
                    .build();
        }

        List<com.paytm.digital.education.database.entity.Exam> examEntityList =
                commonMongoRepository
                        .getEntityFieldsByValuesIn(EXAM_ID, coachingInstituteEntity.getExams(),
                                com.paytm.digital.education.database.entity.Exam.class,
                                CoachingInstituteService.EXAM_FIELDS);

        return TopExamsInstitute
                .builder()
                .header("Top Exams prepared for by " + coachingInstituteEntity.getBrandName())
                .results(CoachingInstituteTransformer.convertExamEntityToDto(examList,
                        examEntityList))
                .build();
    }

    private TopStreams getStreamsForInstitute(CoachingInstituteEntity coachingInstituteEntity) {
        List<Stream> streamList = new ArrayList<>();
        if (CollectionUtils.isEmpty(coachingInstituteEntity.getStreams())) {
            return TopStreams
                    .builder()
                    .header("Streams prepared for by " + coachingInstituteEntity.getBrandName())
                    .results(streamList)
                    .build();
        }

        List<com.paytm.digital.education.database.entity.StreamEntity> streamEntityList =
                commonMongoRepository
                        .getEntityFieldsByValuesIn(STREAM_ID, coachingInstituteEntity.getStreams(),
                                StreamEntity.class, CoachingInstituteService.STREAM_FIELDS);

        return TopStreams
                .builder()
                .header("Streams prepared for by " + coachingInstituteEntity.getBrandName())
                .results(CoachingInstituteTransformer
                        .convertStreamEntityToStreamDto(streamList, streamEntityList))
                .build();
    }

    private TopRankers getTopRankersForInstitute(long instituteId, Long streamId,
            Long examId) {
        Set<Long> examIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();

        final Map<Sort.Direction, String> sortMap = new HashMap<>();
        sortMap.put(Sort.Direction.DESC, "exam_year");
        sortMap.put(Sort.Direction.ASC, "priority");

        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(INSTITUTE_ID, instituteId);

        if (Objects.nonNull(examId)) {
            searchRequest.put(EXAM_ID, examId);
        } else if (Objects.nonNull(streamId)) {
            searchRequest.put(STREAM_IDS, streamId);
        }

        List<TopRankerEntity> topRankerEntityList = commonMongoRepository.findAllAndSortBy(
                searchRequest, TopRankerEntity.class, CoachingCourseService.TOP_RANKER_FIELDS, AND,
                sortMap);

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

        Map<Long, String> examIdsAndNameMap = getExamIdsToNameMap((new ArrayList<>(examIds)));
        Map<Long, String> coachingCourseIdsAndNameMap =
                getCoachingCourseIdsToNameMap((new ArrayList<>(courseIds)));

        return TopRankers
                .builder()
                .header("Top Rankers")
                .results(CoachingInstituteTransformer
                        .convertTopRankerEntityToTopRankerDto(examIdsAndNameMap,
                                coachingCourseIdsAndNameMap,
                                topRankerEntityList, topRankerList))
                .build();
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
        filter.put(STREAM_IDS, Collections.singletonList(streamId));

        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(IGNORE_GLOBAL_PRIORITY, ASC);

        return (List<CoachingInstituteData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.COACHING_INSTITUTE, sortOrder);
    }
}
