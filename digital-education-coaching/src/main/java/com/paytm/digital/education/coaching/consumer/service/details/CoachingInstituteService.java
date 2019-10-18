package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.Faq;
import com.paytm.digital.education.coaching.consumer.model.dto.MockTest;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.SyllabusAndBrochure;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopExamsInstitute;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRankers;
import com.paytm.digital.education.coaching.consumer.model.dto.TopStreams;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseTypeResponse;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.CenterAndBrochureInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.CoachingCourseTypeInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.InstituteCenterSection;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.InstituteMoreInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.InstituteMoreInfoData;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetCoachingInstituteDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.service.search.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingInstituteTransformer;
import com.paytm.digital.education.coaching.enums.CoachingCourseType;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_FIND_CENTERS_LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_TYPE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DONWLOAD_ICON;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_YEAR;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FAQ_LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_COVER_IMAGE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_ENABLED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MockTestBanner.BUTTON_TEXT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MockTestBanner.DESCRIPTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MockTestBanner.HEADER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MockTestBanner.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MockTestBanner.TAG_TEXT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.OTHER_INFO_LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_GLOBAL_PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TOP_RANKER_LIMIT;
import static com.paytm.digital.education.coaching.consumer.service.details.CoachingCourseService.CENTER_FIELDS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.BROWSE_BY_COURSE_TYPE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.DOWNLOAD_BROCHURE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.FAQ;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.FIND_CENTERS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.FIND_CENTERS_DESCRIPTION;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.MORE_FROM;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.OTHER_INFORMATION;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.STREAMS_PREPARED_FOR_BY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_COACHING_COURSES_BY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_EXAMS_PREPARED_FOR_BY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_RANKERS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_INSTITUTE_BROCHURE;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_IMAGE;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_LOGO;
import static com.paytm.digital.education.enums.es.DataSortOrder.ASC;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingInstituteService {

    private static final List<String> COACHING_INSTITUTE_FIELDS =
            Arrays.asList("institute_id", "brand_name", "cover_image", "about_institute",
                    "key_highlights", "streams", "exams", "course_types", "faqs",
                    "more_info1", "more_info2", "more_info3", "more_info4", "logo", "brochure",
                    "is_enabled");

    private static final List<String> EXAM_FIELDS =
            Arrays.asList("exam_id", "exam_full_name", "exam_short_name", "logo", "is_enabled",
                    "stream_ids");

    private static final List<String> STREAM_FIELDS =
            Arrays.asList("stream_id", "name", "logo", "is_enabled");

    private final CommonMongoRepository        commonMongoRepository;
    private final SearchDataHelper             searchDataHelper;
    private final PropertyReader               propertyReader;
    private final CoachingInstituteTransformer coachingInstituteTransformer;

    public GetCoachingInstituteDetailsResponse getCoachingInstituteDetails(long instituteId,
            String urlDisplayKey, Long streamId, Long examId) {
        CoachingInstituteEntity coachingInstituteEntity = commonMongoRepository.getEntityByFields(
                INSTITUTE_ID, instituteId, CoachingInstituteEntity.class,
                COACHING_INSTITUTE_FIELDS);
        if (Objects.isNull(coachingInstituteEntity)
                || Objects.isNull(coachingInstituteEntity.getIsEnabled())
                || !coachingInstituteEntity.getIsEnabled()) {
            log.error("Institute with id: {} does not exist", instituteId);
            throw new BadRequestException(INVALID_INSTITUTE_ID);
        }
        if (!CommonUtils.convertNameToUrlDisplayName(coachingInstituteEntity.getBrandName())
                .equals(urlDisplayKey)) {
            log.error("Institute with url display key: {} does not exist for institute_id: {}",
                    urlDisplayKey, instituteId);
            throw new BadRequestException(INVALID_INSTITUTE_NAME);
        }

        Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        List<String> sections = (List<String>) propertyMap.getOrDefault(INSTITUTE,
                new ArrayList<>());

        final Map<Long, CoachingCenterEntity> coachingCenterIdAndCenterMap =
                this.fetchCoachingCentersByInstituteId(instituteId);

        return GetCoachingInstituteDetailsResponse.builder()
                .instituteId(coachingInstituteEntity.getInstituteId())
                .instituteName(coachingInstituteEntity.getBrandName())
                .description(coachingInstituteEntity.getAboutInstitute())
                .imageUrl(ImageUtils.getImageWithAbsolutePath(
                        coachingInstituteEntity.getCoverImage(), INSTITUTE_COVER_IMAGE_PLACEHOLDER,
                        TOP_COACHING_INSTITUTES_IMAGE))
                .logo(ImageUtils.getImageWithAbsolutePath(coachingInstituteEntity.getLogo(),
                        INSTITUTE_PLACEHOLDER, TOP_COACHING_INSTITUTES_LOGO))
                .instituteHighlights(coachingInstituteTransformer.convertInstituteHighlights(
                        coachingInstituteEntity.getKeyHighlights()))
                .centerAndBrochureInfo(this.getCenterAndBrochureInfo(coachingInstituteEntity,
                        coachingCenterIdAndCenterMap))
                .streams(this.getStreamsForInstitute(coachingInstituteEntity))
                .exams(this.getExamsForInstitute(coachingInstituteEntity))
                .topRankers(this.getTopRankersForInstitute(instituteId, streamId, examId,
                        coachingCenterIdAndCenterMap))
                .coachingCourseTypes(this.getCoachingCourseTypes(coachingInstituteEntity))
                .topCoachingCourses(this.getTopCoachingCoursesForInstitute(coachingInstituteEntity,
                        streamId, examId))
                .mockTest(getMockTestInfo(coachingInstituteEntity.getBrandName()))
                .instituteMoreInfo(this.getInstituteMoreInfo(coachingInstituteEntity))
                .sections(sections)
                .examName(this.getExamName(examId))
                .streamName(this.getStreamName(streamId))
                .build();
    }

    private InstituteMoreInfo getInstituteMoreInfo(
            CoachingInstituteEntity coachingInstituteEntity) {
        List<InstituteMoreInfoData> instituteMoreInfoDataList = new ArrayList<>();
        InstituteMoreInfoData instituteMoreInfoData =
                InstituteMoreInfoData.builder().header(FAQ.getValue()).results(
                        new ArrayList<>(fillFaqs(coachingInstituteEntity.getFaqs()))).logo(FAQ_LOGO)
                        .build();
        instituteMoreInfoDataList.add(instituteMoreInfoData);

        InstituteMoreInfoData otherInfo = this.getOtherInfo(coachingInstituteEntity);
        if (Objects.nonNull(otherInfo)) {
            instituteMoreInfoDataList.add(otherInfo);
        }

        return InstituteMoreInfo.builder()
                .header(String.format(MORE_FROM.getValue(),
                        coachingInstituteEntity.getBrandName()))
                .results(instituteMoreInfoDataList)
                .build();
    }

    private InstituteMoreInfoData getOtherInfo(CoachingInstituteEntity coachingInstituteEntity) {
        List<Object> otherInfo = new ArrayList<>();

        if (!StringUtils.isEmpty(coachingInstituteEntity.getMoreInfo1())) {
            otherInfo.add(coachingInstituteEntity.getMoreInfo1());
        }
        if (!StringUtils.isEmpty(coachingInstituteEntity.getMoreInfo2())) {
            otherInfo.add(coachingInstituteEntity.getMoreInfo2());
        }
        if (!StringUtils.isEmpty(coachingInstituteEntity.getMoreInfo3())) {
            otherInfo.add(coachingInstituteEntity.getMoreInfo3());
        }
        if (!StringUtils.isEmpty(coachingInstituteEntity.getMoreInfo4())) {
            otherInfo.add(coachingInstituteEntity.getMoreInfo4());
        }

        if (CollectionUtils.isEmpty(otherInfo)) {
            return null;
        }
        return InstituteMoreInfoData.builder()
                .header(OTHER_INFORMATION.getValue())
                .results(otherInfo)
                .logo(OTHER_INFO_LOGO)
                .build();
    }

    private String getStreamName(Long streamId) {
        if (Objects.isNull(streamId)) {
            return null;
        }

        com.paytm.digital.education.database.entity.StreamEntity stream =
                commonMongoRepository.getEntityByFields(STREAM_ID, streamId,
                        com.paytm.digital.education.database.entity.StreamEntity.class,
                        STREAM_FIELDS);
        if (Objects.isNull(stream)
                || Objects.isNull(stream.getIsEnabled()) || !stream.getIsEnabled()) {
            return null;
        }
        return stream.getName();
    }

    private String getExamName(Long examId) {
        if (Objects.isNull(examId)) {
            return null;
        }

        com.paytm.digital.education.database.entity.Exam exam = this.getExamEntity(examId);
        if (Objects.isNull(exam)) {
            return null;
        }
        return exam.getExamShortName();
    }

    private com.paytm.digital.education.database.entity.Exam getExamEntity(Long examId) {
        return commonMongoRepository.getEntityByFields(EXAM_ID, examId,
                com.paytm.digital.education.database.entity.Exam.class, EXAM_FIELDS);
    }

    private Map<Long, CoachingCenterEntity> fetchCoachingCentersByInstituteId(
            final long instituteId) {
        List<CoachingCenterEntity> coachingCenterEntityList = this.commonMongoRepository
                .getEntitiesByIdAndFields(INSTITUTE_ID, instituteId, CoachingCenterEntity.class,
                        CENTER_FIELDS);

        if (CollectionUtils.isEmpty(coachingCenterEntityList)) {
            return Collections.EMPTY_MAP;
        }

        return coachingCenterEntityList.stream()
                .collect(Collectors.toMap(CoachingCenterEntity::getCenterId, center -> center));
    }

    public static MockTest getMockTestInfo(String name) {
        return MockTest.builder()
                .logo(LOGO)
                .header(String.format(HEADER, name))
                .description(DESCRIPTION)
                .tagText(TAG_TEXT)
                .buttonText(BUTTON_TEXT)
                .redirectUrl("")
                .build();
    }

    private CenterAndBrochureInfo getCenterAndBrochureInfo(
            CoachingInstituteEntity coachingInstituteEntity,
            Map<Long, CoachingCenterEntity> coachingCenterIdAndCenterMap) {
        return CenterAndBrochureInfo.builder()
                .centers(!CollectionUtils.isEmpty(coachingCenterIdAndCenterMap)
                        ? InstituteCenterSection.builder()
                        .header(FIND_CENTERS.getValue())
                        .description(String.format(FIND_CENTERS_DESCRIPTION.getValue(),
                                coachingInstituteEntity.getBrandName()))
                        .logo(COACHING_INSTITUTE_FIND_CENTERS_LOGO)
                        .build() : null)
                .brochure(SyllabusAndBrochure.builder()
                        .header(DOWNLOAD_BROCHURE.getValue())
                        .url(CommonUtil.getAbsoluteUrl(coachingInstituteEntity.getBrochure(),
                                COACHING_INSTITUTE_BROCHURE))
                        .logo(DONWLOAD_ICON)
                        .build())
                .build();
    }

    private CoachingCourseTypeInfo getCoachingCourseTypes(
            CoachingInstituteEntity coachingInstituteEntity) {
        List<CoachingCourseTypeResponse> listOfCourseType = new ArrayList<>();
        if (Objects.nonNull(coachingInstituteEntity.getCourseTypes())) {
            for (CourseType courseType : coachingInstituteEntity.getCourseTypes()) {
                CoachingCourseTypeResponse toAdd = CoachingCourseType.getStaticDataByCourseType(
                        courseType);

                Map<String, List<Object>> filter = new HashMap<>();
                filter.put(COACHING_COURSE_INSTITUTE,
                        Collections.singletonList(coachingInstituteEntity.getBrandName()));
                filter.put(COURSE_TYPE, Collections.singletonList(courseType));
                toAdd.setFilter(filter);
                listOfCourseType.add(toAdd);
            }
        }
        return CoachingCourseTypeInfo.builder()
                .header(BROWSE_BY_COURSE_TYPE.getValue())
                .results(listOfCourseType)
                .build();
    }

    private TopCoachingCourses getTopCoachingCoursesForInstitute(
            CoachingInstituteEntity coachingInstituteEntity, Long streamId, Long examId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(COACHING_INSTITUTE_ID, Collections.singletonList(
                coachingInstituteEntity.getInstituteId()));

        if (Objects.nonNull(examId)) {
            filter.put(EXAM_IDS, Collections.singletonList(examId));
        } else if (Objects.nonNull(streamId)) {
            filter.put(STREAM_IDS, Collections.singletonList(streamId));
        }

        List<CoachingCourseData> topCoachingCourses =
                (List<CoachingCourseData>) (List<?>) searchDataHelper.getTopSearchData(filter,
                        EducationEntity.COACHING_COURSE, null);

        if (CollectionUtils.isEmpty(topCoachingCourses)) {
            topCoachingCourses = new ArrayList<>();
        }

        Map<String, List<Object>> searchFilter = new HashMap<>();
        searchFilter.put(COACHING_COURSE_INSTITUTE,
                Collections.singletonList(coachingInstituteEntity.getBrandName()));

        return TopCoachingCourses.builder()
                .header(String.format(TOP_COACHING_COURSES_BY.getValue(),
                        coachingInstituteEntity.getBrandName()))
                .results(topCoachingCourses)
                .filter(searchFilter)
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
            return TopExamsInstitute.builder()
                    .header(String.format(TOP_EXAMS_PREPARED_FOR_BY.getValue(),
                            coachingInstituteEntity.getBrandName()))
                    .results(examList)
                    .build();
        }

        List<com.paytm.digital.education.database.entity.Exam> examEntityList =
                commonMongoRepository.getEntityFieldsByValuesIn(EXAM_ID,
                        coachingInstituteEntity.getExams(),
                        com.paytm.digital.education.database.entity.Exam.class,
                        CoachingInstituteService.EXAM_FIELDS);

        return TopExamsInstitute.builder()
                .header(String.format(TOP_EXAMS_PREPARED_FOR_BY.getValue(),
                        coachingInstituteEntity.getBrandName()))
                .results(coachingInstituteTransformer.convertExamEntityToDto(examList,
                        examEntityList))
                .build();
    }

    private TopStreams getStreamsForInstitute(CoachingInstituteEntity coachingInstituteEntity) {
        List<Stream> streamList = new ArrayList<>();
        if (CollectionUtils.isEmpty(coachingInstituteEntity.getStreams())) {
            return TopStreams.builder()
                    .header(String.format(STREAMS_PREPARED_FOR_BY.getValue(),
                            coachingInstituteEntity.getBrandName()))
                    .results(streamList)
                    .build();
        }

        List<com.paytm.digital.education.database.entity.StreamEntity> streamEntityList =
                commonMongoRepository.getEntityFieldsByValuesIn(STREAM_ID,
                        coachingInstituteEntity.getStreams(), StreamEntity.class,
                        CoachingInstituteService.STREAM_FIELDS);

        return TopStreams.builder()
                .header(String.format(STREAMS_PREPARED_FOR_BY.getValue(),
                        coachingInstituteEntity.getBrandName()))
                .results(coachingInstituteTransformer
                        .convertStreamEntityToStreamDto(streamList, streamEntityList))
                .build();
    }

    private TopRankers getTopRankersForInstitute(long instituteId, Long streamId, Long examId,
            final Map<Long, CoachingCenterEntity> coachingCenterIdAndCenterMap) {
        Set<Long> examIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();

        final Map<Sort.Direction, String> sortMap = new HashMap<>();
        sortMap.put(Sort.Direction.DESC, EXAM_YEAR);
        sortMap.put(Sort.Direction.ASC, PRIORITY);

        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(INSTITUTE_ID, instituteId);
        searchRequest.put(IS_ENABLED, true);

        if (Objects.nonNull(examId)) {
            searchRequest.put(EXAM_ID, examId);
        } else if (Objects.nonNull(streamId)) {
            searchRequest.put(STREAM_IDS, streamId);
        }

        List<TopRankerEntity> topRankerEntityList = commonMongoRepository.findAllAndSortBy(
                searchRequest, TopRankerEntity.class, CoachingCourseService.TOP_RANKER_FIELDS, AND,
                sortMap, TOP_RANKER_LIMIT);

        if (CollectionUtils.isEmpty(topRankerEntityList) && searchRequest.containsKey(EXAM_ID)) {
            com.paytm.digital.education.database.entity.Exam examEntity =
                    this.getExamEntity(examId);

            if (Objects.nonNull(examEntity) && !CollectionUtils.isEmpty(examEntity.getStreamIds())
                    && Objects.nonNull(examEntity.getIsEnabled())
                    && examEntity.getIsEnabled()) {
                searchRequest.remove(EXAM_ID);
                searchRequest.put(STREAM_IDS, examEntity.getStreamIds().get(0));

                topRankerEntityList = commonMongoRepository.findAllAndSortBy(
                        searchRequest, TopRankerEntity.class,
                        CoachingCourseService.TOP_RANKER_FIELDS, AND,
                        sortMap, TOP_RANKER_LIMIT);
            }
        }

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
        Map<Long, String> coachingCourseIdsAndNameMap = getCoachingCourseIdsToNameMap(
                new ArrayList<>(courseIds));

        return TopRankers.builder()
                .header(TOP_RANKERS.getValue())
                .results(coachingInstituteTransformer.convertTopRankerEntityToTopRankerDto(
                        examIdsAndNameMap, coachingCourseIdsAndNameMap, topRankerEntityList,
                        topRankerList, coachingCenterIdAndCenterMap))
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
        List<CoachingCourseEntity> coachingCourseEntityList = commonMongoRepository
                .getEntityFieldsByValuesIn(COURSE_ID, courseIds, CoachingCourseEntity.class,
                        coachingCourseFields);
        if (!CollectionUtils.isEmpty(coachingCourseEntityList)) {
            for (CoachingCourseEntity coachingCourseEntity : coachingCourseEntityList) {
                coachingCourseIdsAndNameMap.put(coachingCourseEntity.getCourseId(),
                        coachingCourseEntity.getName());
            }
        }
        return coachingCourseIdsAndNameMap;
    }

    List<CoachingInstituteData> getTopCoachingInstitutesByExamId(Long examId) {
        return getCoachingInstituteData(examId, EXAM_IDS);
    }

    List<CoachingInstituteData> getTopCoachingInstitutesByStreamId(Long streamId) {
        return getCoachingInstituteData(streamId, STREAM_IDS);
    }

    private List<CoachingInstituteData> getCoachingInstituteData(Long id, String idFieldName) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(idFieldName, Collections.singletonList(id));

        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(IGNORE_GLOBAL_PRIORITY, ASC);

        return (List<CoachingInstituteData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.COACHING_INSTITUTE, sortOrder);
    }
}
