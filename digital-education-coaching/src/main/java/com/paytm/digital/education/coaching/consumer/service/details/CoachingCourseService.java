package com.paytm.digital.education.coaching.consumer.service.details;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.SyllabusAndBrochure;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRankers;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFeature;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFeatures;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFee;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseHighlight;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseImportantDates;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CourseGetStarted;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.TaxBreakup;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.TaxBreakupInfo;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetCoachingCourseDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.service.search.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.transformer.CoachingCourseTransformer;
import com.paytm.digital.education.coaching.enums.CourseSessionDetails;
import com.paytm.digital.education.coaching.utils.ComparisonUtils;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.dao.CoachingCenterDAO;
import com.paytm.digital.education.database.dao.CoachingCourseDAO;
import com.paytm.digital.education.database.dao.CoachingCtaDAO;
import com.paytm.digital.education.database.dao.CoachingExamDAO;
import com.paytm.digital.education.database.dao.CoachingInstituteDAO;
import com.paytm.digital.education.database.dao.TopRankerDAO;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CoachingCourseFeatureRepository;
import com.paytm.digital.education.enums.CTAViewType;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.Language;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTER_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_VERTICAL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DETAILS_PROPERTY_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DONWLOAD_ICON;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_YEAR;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TOP_ELEMENTS_ANY_PAGE_LIMIT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_CGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_IGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_SGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_UTGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_CGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_IGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_SGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_UTGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.CGST_HEADER;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.CGST_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.CGST_OMS_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.COURSE_DETAILS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.COURSE_FEATURES_AVAILABLE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.COURSE_FEE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.COURSE_HOW_TO_GET_STARTED;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.COURSE_TYPE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.DOUBT_SOLVING_SESSIONS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.DOWNLOAD_SYLLABUS_AND_BROCHURE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.DURATION_COURSE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.ELIGIBILITY_COURSE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.IGST_HEADER;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.IGST_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.IGST_OMS_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.IMPORTANT_DATES;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.LANGUAGE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.PROGRESS_ANALYSIS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.PROVIDES_CERTIFICATE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.RANK_ANALYSIS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.SGST_HEADER;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.SGST_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.SGST_OMS_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TARGET_EXAM_COURSE;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TEACHER_STUDENT_RATIO;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TOP_RANKERS;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.UTGST_HEADER;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.UTGST_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.UTGST_OMS_KEY;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.VALIDITY_COURSE;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSE_BROCHURE;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_LOGO;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_COURSE_ID_AND_URL_DISPLAY_KEY;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;

@Service
public class CoachingCourseService {

    private static final Logger log = LoggerFactory.getLogger(CoachingCourseService.class);

    private static final String TARGET_EXAM     = "TARGET_EXAM";
    private static final String TOP_RANKER_EXAM = "TOP_RANKER_EXAM";

    @Value("${coaching.category.id}")
    private String categoryId;

    private static final List<String> COURSE_FIELDS =
            Arrays.asList("faqs", "course_id", "name", "coaching_institute_id", "course_type",
                    "stream_ids", "primary_exam_ids", "duration_type", "duration", "eligibility",
                    "info", "description", "original_price", "discounted_price", "level",
                    "course_cover", "language", "syllabus", "important_dates", "how_to_use_1",
                    "how_to_use_2", "how_to_use_3", "how_to_use_4", "is_certificate_available",
                    "is_doubt_solving_forum_available", "is_progress_analysis_available",
                    "is_rank_analysis_available", "course_features",
                    "classroom_teacher_student_ratio", "test_count", "test_duration",
                    "test_question_count", "test_practice_paper_count",
                    "distance_learning_books_count", "distance_learning_solved_paper_count",
                    "distance_learning_assignment_count", "elearning_lecture_count",
                    "elearning_lecture_duration", "elearning_online_test_count",
                    "elearning_practice_paper_count", "classroom_lecture_count",
                    "classroom_lecture_duration", "classroom_test_count", "sgst", "cgst", "igst",
                    "tcs", "merchant_product_id", "is_enabled", "validity", "validity_type",
                    "paytm_product_id", "cta_info");

    private static final List<String> EXAM_FIELDS =
            Arrays.asList("exam_id", "exam_full_name", "exam_short_name", "conducting_body",
                    "priority", "logo");

    static final List<String> TOP_RANKER_FIELDS =
            Arrays.asList("top_ranker_id", "institute_id", "center_id", "course_ids",
                    "exam_id", "exam_name", "student_name", "student_photo",
                    "rank_obtained", "exam_year", "testimonial", "priority");

    private static final List<String> INSTITUTE_FIELDS =
            Arrays.asList("institute_id", "brand_name", "logo");

    public static final List<String> CENTER_FIELDS =
            Arrays.asList("institute_id", "center_id", "official_name", "official_address",
                    "course_types", "opening_time", "closing_time", "center_image");

    @Autowired
    private CoachingCourseTransformer       coachingCourseTransformer;
    @Autowired
    private SearchDataHelper                searchDataHelper;
    @Autowired
    private PropertyReader                  propertyReader;
    @Autowired
    private CoachingCourseFeatureRepository coachingCourseFeatureRepository;
    @Autowired
    private CoachingCourseDAO               coachingCourseDAO;
    @Autowired
    private CoachingInstituteDAO            coachingInstituteDAO;
    @Autowired
    private CoachingExamDAO                 coachingExamDAO;
    @Autowired
    private TopRankerDAO                    topRankerDAO;
    @Autowired
    private CoachingCenterDAO               coachingCenterDAO;
    @Autowired
    private CoachingCtaDAO                  coachingCtaDAO;

    public GetCoachingCourseDetailsResponse getCourseDetailsByIdAndUrlDisplayKey(
            final long courseId, final String urlDisplayKey) {

        final CoachingCourseEntity course = this.fetchCourse(courseId, urlDisplayKey);
        final Long instituteId = course.getCoachingInstituteId();

        final CoachingInstituteEntity institute = this.fetchInstitute(instituteId);

        final Map<Long, String> examIdAndNameMap = new HashMap<>();
        final List<Long> topRankerExamIds = new ArrayList<>();

        final List<TopRankerEntity> topRankerEntityList = this.fetchTopRankers(courseId,
                institute.getInstituteId());

        final Map<Long, String> courseIdAndNameMap = this.buildCourseIdAndNameMapAndFillExamIds(
                topRankerEntityList, topRankerExamIds);

        final Map<String, List<Exam>> examTypeAndExamListMap = this.buildExamIdAndExamListMap(
                course, examIdAndNameMap, topRankerExamIds);

        final Map<Long, CoachingCenterEntity> centerIdAndCenterMap = this.fetchCenterByCenterIds(
                topRankerEntityList);

        final Map<Long, CoachingCtaEntity> ctaIdToCtaMap;
        if (!CollectionUtils.isEmpty(course.getCtaInfo())
                && !CollectionUtils.isEmpty(course.getCtaInfo().values())) {
            ctaIdToCtaMap = this.fetchCtaIdToCtaMapByCtaIds(course.getCtaInfo().values()
                    .stream().flatMap(List::stream).collect(Collectors.toList()));
        } else {
            ctaIdToCtaMap = Collections.emptyMap();
        }

        return this.buildResponse(course, institute, examTypeAndExamListMap,
                topRankerEntityList, centerIdAndCenterMap, examIdAndNameMap, courseIdAndNameMap,
                ctaIdToCtaMap, this.fetchCoachingCourseFeatures(course.getCourseFeatureIds()),
                this.fetchSections());
    }

    private Map<Long, CoachingCtaEntity> fetchCtaIdToCtaMapByCtaIds(Collection<Long> ctaIds) {

        List<CoachingCtaEntity> ctaList = this.fetchCtaByCtaIds(new ArrayList<>(ctaIds));

        if (!ctaList.isEmpty()) {
            return ctaList.stream().collect(Collectors.toMap(cta -> cta.getCtaId(),
                    Function.identity()));
        }

        return Collections.emptyMap();
    }

    private Map<Long, String> buildCourseIdAndNameMapAndFillExamIds(
            final List<TopRankerEntity> topRankerEntityList, final List<Long> topRankerExamIds) {

        Map<Long, String> courseIdAndNameMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(topRankerEntityList)) {
            final List<Long> courseIds = new ArrayList<>();
            for (final TopRankerEntity tr : topRankerEntityList) {
                topRankerExamIds.add(tr.getExamId());
                courseIds.addAll(tr.getCourseIds());
            }
            courseIdAndNameMap = this.getCourseIdAndNameMap(courseIds);
        }
        return courseIdAndNameMap;
    }

    private Map<String, List<Exam>> buildExamIdAndExamListMap(CoachingCourseEntity course,
            Map<Long, String> examIdAndNameMap, List<Long> topRankerExamIds) {
        final Map<String, List<Exam>> examTypeAndExamListMap = this.fetchExamTypeAndExamListMap(
                course.getPrimaryExamIds(),
                topRankerExamIds);

        if (!examTypeAndExamListMap.isEmpty()) {
            final List<Exam> topRankerExams = examTypeAndExamListMap.get(TOP_RANKER_EXAM);
            if (!topRankerExams.isEmpty()) {
                for (final Exam exam : topRankerExams) {
                    examIdAndNameMap.put(exam.getId(), exam.getExamShortName());
                }
            }
        }
        return examTypeAndExamListMap;
    }

    private List<CoachingCourseFeature> fetchCoachingCourseFeatures(
            final List<Long> courseFeatureIds) {
        if (CollectionUtils.isEmpty(courseFeatureIds)) {
            return Collections.emptyList();
        }
        return this.coachingCourseTransformer.convertCourseFeatures(
                this.coachingCourseFeatureRepository.findByCoachingCourseFeatureIdIn(
                        courseFeatureIds));
    }

    private CoachingCourseEntity fetchCourse(final long courseId, final String urlDisplayKey) {

        final List<CoachingCourseEntity> coachingCourseEntityList =
                coachingCourseDAO.findByCourseId(COACHING_COURSE_ID, courseId,
                        CoachingCourseService.COURSE_FIELDS, CoachingCourseService.COURSE_FIELDS);

        if (CollectionUtils.isEmpty(coachingCourseEntityList)
                || coachingCourseEntityList.size() > 1
                || !urlDisplayKey.equalsIgnoreCase(CommonUtils.convertNameToUrlDisplayName(
                coachingCourseEntityList.get(0).getName()))
                || !coachingCourseEntityList.get(0).getIsEnabled()) {
            log.error("Got no coachingCourse for courseId: {}, urlDisplayKey: {}",
                    courseId, urlDisplayKey);
            throw new BadRequestException(INVALID_COURSE_ID_AND_URL_DISPLAY_KEY,
                    INVALID_COURSE_ID_AND_URL_DISPLAY_KEY.getExternalMessage());
        }
        return coachingCourseEntityList.get(0);
    }

    private CoachingInstituteEntity fetchInstitute(final long instituteId) {
        final CoachingInstituteEntity institute =
                coachingInstituteDAO.findByInstituteId(INSTITUTE_ID, instituteId, INSTITUTE_FIELDS,
                        INSTITUTE_FIELDS);
        if (institute == null) {
            log.error("Got null CoachingInstitute for id: {}", instituteId);
            throw new BadRequestException(INVALID_INSTITUTE_ID,
                    INVALID_INSTITUTE_ID.getExternalMessage());
        }
        return institute;
    }

    private Map<Long, String> getCourseIdAndNameMap(final List<Long> coachingCourseIdList) {

        List<String> fields = new ArrayList<>();
        fields.add(NAME);
        fields.add(COURSE_ID);

        final List<CoachingCourseEntity> coachingCourseEntityList = this.fetchCourses(
                coachingCourseIdList, fields);
        if (CollectionUtils.isEmpty(coachingCourseEntityList)) {
            return new HashMap<>();
        }
        final Map<Long, String> courseIdAndNameMap = new HashMap<>();
        for (final CoachingCourseEntity course : coachingCourseEntityList) {
            courseIdAndNameMap.put(course.getCourseId(), course.getName());
        }
        return courseIdAndNameMap;
    }

    private List<CoachingCourseEntity> fetchCourses(final List<Long> coachingCourseIdList,
            final List<String> fields) {
        return coachingCourseDAO.findByCourseIdsIn(COURSE_ID, coachingCourseIdList, fields, fields);
    }

    private Map<String, List<Exam>> fetchExamTypeAndExamListMap(List<Long> targetExamIdList,
            final List<Long> topRankerExamIds) {
        final List<Long> examIdList = new ArrayList<>(topRankerExamIds);
        if (!CollectionUtils.isEmpty(targetExamIdList)) {
            examIdList.addAll(targetExamIdList);
        } else {
            targetExamIdList = new ArrayList<>();
        }

        final Map<String, List<Exam>> examTypeAndExamListMap = new HashMap<>();
        examTypeAndExamListMap.put(TARGET_EXAM, new ArrayList<>());
        examTypeAndExamListMap.put(TOP_RANKER_EXAM, new ArrayList<>());

        final List<com.paytm.digital.education.database.entity.Exam> exams =
                this.fetchExamsByExamIds(examIdList);
        if (CollectionUtils.isEmpty(exams)) {
            log.warn("Got no exams for examIds: {}", examIdList);
            return examTypeAndExamListMap;
        }

        final Set<Long> targetExamIdsSet = new HashSet<>(targetExamIdList);
        for (final com.paytm.digital.education.database.entity.Exam exam : exams) {
            if (targetExamIdsSet.contains(exam.getExamId())) {
                examTypeAndExamListMap.get(TARGET_EXAM)
                        .add(this.coachingCourseTransformer.convertExam(exam));
            }
            if (topRankerExamIds.contains(exam.getExamId())) {
                examTypeAndExamListMap.get(TOP_RANKER_EXAM)
                        .add(this.coachingCourseTransformer.convertExam(exam));
            }
        }
        return examTypeAndExamListMap;
    }

    private List<CoachingCtaEntity> fetchCtaByCtaIds(final List<Long> ctaIdList) {
        return coachingCtaDAO.findAllByCtaIdIn(ctaIdList);
    }

    private List<com.paytm.digital.education.database.entity.Exam> fetchExamsByExamIds(
            final List<Long> examIdList) {
        return coachingExamDAO.findByExamIdsIn(EXAM_ID, examIdList,
                CoachingCourseService.EXAM_FIELDS, CoachingCourseService.EXAM_FIELDS);
    }

    private List<TopRankerEntity> fetchTopRankers(final long courseId, final Long instituteId) {

        final Map<Sort.Direction, String> sortMap = new LinkedHashMap<>();
        sortMap.put(Sort.Direction.DESC, EXAM_YEAR);
        sortMap.put(Sort.Direction.ASC, PRIORITY);

        List<TopRankerEntity> topRankerEntityList =
                topRankerDAO.findByCourseIdsInAndSortBy(COACHING_COURSE_IDS,
                        Collections.singletonList(courseId),
                        TOP_RANKER_FIELDS, sortMap, TOP_RANKER_FIELDS);

        if (CollectionUtils.isEmpty(topRankerEntityList)) {
            log.warn("Got no topRankers for courseId: {}", courseId);
            topRankerEntityList =
                    topRankerDAO.findByInstituteIdsInAndSortBy(INSTITUTE_ID,
                            Collections.singletonList(instituteId),
                            CoachingCourseService.TOP_RANKER_FIELDS, sortMap,
                            CoachingCourseService.TOP_RANKER_FIELDS);
            if (CollectionUtils.isEmpty(topRankerEntityList)) {
                log.warn("Got no topRankers for instituteId: {}", instituteId);
                return new ArrayList<>();
            }
        }
        return topRankerEntityList;
    }

    private Map<Long, CoachingCenterEntity> fetchCenterByCenterIds(
            final List<TopRankerEntity> topRankerEntityList) {
        if (CollectionUtils.isEmpty(topRankerEntityList)) {
            return Collections.EMPTY_MAP;
        }

        final List<Long> centerIdList = topRankerEntityList.stream()
                .map(TopRankerEntity::getCenterId)
                .collect(Collectors.toList());

        List<CoachingCenterEntity> coachingCenterEntityList =
                coachingCenterDAO.findByCenterIdsIn(CENTER_ID, centerIdList,
                        CENTER_FIELDS, CENTER_FIELDS);

        if (CollectionUtils.isEmpty(coachingCenterEntityList)) {
            return Collections.EMPTY_MAP;
        }

        return coachingCenterEntityList.stream()
                .collect(Collectors.toMap(CoachingCenterEntity::getCenterId, center -> center));
    }

    private GetCoachingCourseDetailsResponse buildResponse(final CoachingCourseEntity course,
            final CoachingInstituteEntity institute,
            final Map<String, List<Exam>> examTypeAndExamListMap,
            final List<TopRankerEntity> topRankers,
            final Map<Long, CoachingCenterEntity> centerIdAndCenterMap,
            final Map<Long, String> examIdAndNameMap,
            final Map<Long, String> courseIdAndNameMap,
            final Map<Long, CoachingCtaEntity> ctaIdToCtaMap,
            final List<CoachingCourseFeature> coachingCourseFeatures,
            final List<String> sections) {

        List<CoachingCourseHighlight> courseHighlights = new ArrayList<>();
        String targetExam = this.fillTargetExam(examTypeAndExamListMap);
        if (!StringUtils.isEmpty(targetExam)) {
            courseHighlights.add(new CoachingCourseHighlight(TARGET_EXAM_COURSE.getValue(),
                    targetExam));
        }
        courseHighlights.add(new CoachingCourseHighlight(ELIGIBILITY_COURSE.getValue(),
                course.getEligibility()));
        courseHighlights.add(CoachingCourseHighlight.builder()
                .key(DURATION_COURSE.getValue())
                .value(course.getDuration() + " " + (null == course.getDurationType()
                        ? EMPTY_STRING : course.getDurationType().getText()))
                .build());
        if (Objects.nonNull(course.getValidity()) && Objects.nonNull(course.getValidityType())) {
            courseHighlights.add(CoachingCourseHighlight.builder()
                    .key(VALIDITY_COURSE.getValue())
                    .value(course.getValidity() + " " + course.getValidityType().getText())
                    .build());
        }

        float convFee =
                (course.getDiscountedPrice().floatValue() * CONVENIENCE_FEE_PERCENTAGE) / 100;
        TaxBreakup convFeeTaxInfo = this.getConvFeeTaxInfo(convFee);
        TaxBreakup taxInfo = this.getTaxInfo(course.getDiscountedPrice().floatValue());

        Map<CTAViewType, List<CoachingCtaEntity>> ctaMap;

        if (!CollectionUtils.isEmpty(course.getCtaInfo())) {
            ctaMap = new HashMap<>(course.getCtaInfo().size());
            for (Map.Entry<CTAViewType, List<Long>> entry : course.getCtaInfo().entrySet()) {
                List<Long> ctaIdList = entry.getValue();
                List<CoachingCtaEntity> ctaList = new ArrayList<>();
                for (Long ctaId : ctaIdList) {
                    ctaList.add(ctaIdToCtaMap.get(ctaId));
                }
                ctaMap.put(entry.getKey(), ctaList);
            }
        } else {
            ctaMap = Collections.emptyMap();
        }

        return GetCoachingCourseDetailsResponse.builder()
                .courseId(course.getCourseId())
                .courseName(course.getName())
                .courseLogo(ImageUtils.getImageWithAbsolutePath(institute.getLogo(),
                        INSTITUTE_PLACEHOLDER, TOP_COACHING_INSTITUTES_LOGO))
                .courseDescription(course.getDescription())
                .paytmProductId(course.getPaytmProductId())
                .merchantProductId(course.getMerchantProductId())
                .categoryId(categoryId)
                .educationVertical(COACHING_VERTICAL_NAME)
                .coachingInstituteId(course.getCoachingInstituteId())
                .coachingInstituteName(institute.getBrandName())
                .topRankers(TopRankers.builder()
                        .header(TOP_RANKERS.getValue())
                        .results(this.coachingCourseTransformer.convertTopRankers(topRankers,
                                examIdAndNameMap, courseIdAndNameMap, centerIdAndCenterMap))
                        .build())
                .importantDates(CoachingCourseImportantDates.builder()
                        .header(IMPORTANT_DATES.getValue())
                        .results(this.coachingCourseTransformer.convertImportantDates(
                                course.getImportantDates()))
                        .build())
                .courseFeatures(CoachingCourseFeatures.builder()
                        .header(COURSE_FEATURES_AVAILABLE.getValue())
                        .results(coachingCourseFeatures)
                        .build())
                .coachingCourseDetails(CoachingCourseDetails.builder()
                        .header(COURSE_DETAILS.getValue())
                        .courseDetailsInfo(this.getCourseDetailsInfo(course))
                        .courseDetailsMoreInfo(this.getMoreInfoMap(course))
                        .syllabusAndBrochure(SyllabusAndBrochure.builder()
                                .header(DOWNLOAD_SYLLABUS_AND_BROCHURE.getValue())
                                .url(CommonUtil.getAbsoluteUrl(course.getSyllabus(),
                                        COACHING_COURSE_BROCHURE))
                                .logo(DONWLOAD_ICON)
                                .build())
                        .build())
                .mockTest(CoachingInstituteService.getMockTestInfo(institute.getBrandName()))
                .coachingCourseFee(CoachingCourseFee.builder()
                        .header(COURSE_FEE.getValue())
                        .currency(Currency.INR.name())
                        .originalPrice(course.getOriginalPrice().floatValue())
                        .discountedPrice(course.getDiscountedPrice().floatValue())
                        .discountPercentage(this.calculateDiscountPercentage(
                                course.getOriginalPrice(), course.getDiscountedPrice()))
                        .taxInfo(taxInfo.getTaxInfo())
                        .totalTax(taxInfo.getTotalTax())
                        .convFee(convFee)
                        .convFeeTaxInfo(convFeeTaxInfo.getTaxInfo())
                        .totalConvFeeTax(convFeeTaxInfo.getTotalTax())
                        .build())
                .courseGetStarted(CourseGetStarted.builder()
                        .header(String.format(COURSE_HOW_TO_GET_STARTED.getValue(),
                                course.getName()))
                        .results(this.buildHowToGetStarted(course))
                        .build())
                .courseHighlights(courseHighlights)
                .sections(sections)
                .ctaMap(ctaMap)
                .build();
    }

    private TaxBreakup getConvFeeTaxInfo(final float convFee) {
        float convFeeIGST = (convFee * CONVENIENCE_FEE_IGST_PERCENTAGE) / 100;
        float convFeeCGST = (convFee * CONVENIENCE_FEE_CGST_PERCENTAGE) / 100;
        float convFeeSGST = (convFee * CONVENIENCE_FEE_SGST_PERCENTAGE) / 100;
        float convFeeUTGST = (convFee * CONVENIENCE_FEE_UTGST_PERCENTAGE) / 100;

        List<TaxBreakupInfo> taxInfo = new ArrayList<>();
        taxInfo.add(TaxBreakupInfo.builder()
                .key(CGST_KEY.getValue())
                .omsKey(CGST_OMS_KEY.getValue())
                .header(CGST_HEADER.getValue())
                .value(convFeeCGST)
                .build());

        taxInfo.add(TaxBreakupInfo.builder()
                .key(IGST_KEY.getValue())
                .omsKey(IGST_OMS_KEY.getValue())
                .header(IGST_HEADER.getValue())
                .value(convFeeIGST)
                .build());

        taxInfo.add(TaxBreakupInfo.builder()
                .key(SGST_KEY.getValue())
                .omsKey(SGST_OMS_KEY.getValue())
                .header(SGST_HEADER.getValue())
                .value(convFeeSGST)
                .build());

        taxInfo.add(TaxBreakupInfo.builder()
                .key(UTGST_KEY.getValue())
                .omsKey(UTGST_OMS_KEY.getValue())
                .header(UTGST_HEADER.getValue())
                .value(convFeeUTGST)
                .build());

        return TaxBreakup.builder()
                .taxInfo(taxInfo)
                .totalTax(convFeeCGST + convFeeIGST + convFeeSGST + convFeeUTGST)
                .build();
    }

    private TaxBreakup getTaxInfo(final float sellingPrice) {
        float itemIGST = (sellingPrice * ITEM_IGST_PERCENTAGE) / 100;
        float itemCGST = (sellingPrice * ITEM_CGST_PERCENTAGE) / 100;
        float itemSGST = (sellingPrice * ITEM_SGST_PERCENTAGE) / 100;
        float itemUTGST = (sellingPrice * ITEM_UTGST_PERCENTAGE) / 100;

        List<TaxBreakupInfo> taxInfo = new ArrayList<>();
        taxInfo.add(TaxBreakupInfo.builder()
                .key(CGST_KEY.getValue())
                .omsKey(CGST_OMS_KEY.getValue())
                .header(CGST_HEADER.getValue())
                .value(itemCGST)
                .build());

        taxInfo.add(TaxBreakupInfo.builder()
                .key(IGST_KEY.getValue())
                .omsKey(IGST_OMS_KEY.getValue())
                .header(IGST_HEADER.getValue())
                .value(itemIGST)
                .build());

        taxInfo.add(TaxBreakupInfo.builder()
                .key(SGST_KEY.getValue())
                .omsKey(SGST_OMS_KEY.getValue())
                .header(SGST_HEADER.getValue())
                .value(itemSGST)
                .build());

        taxInfo.add(TaxBreakupInfo.builder()
                .key(UTGST_KEY.getValue())
                .omsKey(UTGST_OMS_KEY.getValue())
                .header(UTGST_HEADER.getValue())
                .value(itemUTGST)
                .build());

        return TaxBreakup.builder()
                .taxInfo(taxInfo)
                .totalTax(itemCGST + itemCGST + itemSGST + itemUTGST)
                .build();
    }

    private List<String> buildHowToGetStarted(final CoachingCourseEntity course) {
        List<String> infoList = new ArrayList<>();

        if (!StringUtils.isEmpty(course.getHowToUse1())) {
            infoList.add(course.getHowToUse1());
        }
        if (!StringUtils.isEmpty(course.getHowToUse2())) {
            infoList.add(course.getHowToUse2());
        }
        if (!StringUtils.isEmpty(course.getHowToUse3())) {
            infoList.add(course.getHowToUse3());
        }
        if (!StringUtils.isEmpty(course.getHowToUse4())) {
            infoList.add(course.getHowToUse4());
        }
        return infoList;
    }

    private Map<String, String> getCourseDetailsInfo(final CoachingCourseEntity course) {

        final Map<String, String> infoMap = new LinkedHashMap<>();
        infoMap.put(COURSE_TYPE.getValue(), null == course.getCourseType()
                ? EMPTY_STRING : course.getCourseType().getText());
        infoMap.put(LANGUAGE.getValue(),
                convertLanguageEnumListToTextList(course.getLanguage()));
        infoMap.put(PROVIDES_CERTIFICATE.getValue(), this.coachingCourseTransformer
                .convertBooleanToString(course.getIsCertificateAvailable()));
        infoMap.put(DOUBT_SOLVING_SESSIONS.getValue(),
                this.coachingCourseTransformer.convertBooleanToString(
                        course.getIsDoubtSolvingForumAvailable()));
        infoMap.put(PROGRESS_ANALYSIS.getValue(), this.coachingCourseTransformer
                .convertBooleanToString(course.getIsProgressAnalysisAvailable()));
        infoMap.put(RANK_ANALYSIS.getValue(), this.coachingCourseTransformer
                .convertBooleanToString(course.getIsRankAnalysisAvailable()));

        return infoMap;
    }

    private Map<String, String> getMoreInfoMap(final CoachingCourseEntity course) {
        final Map<String, String> courseMoreInfoMap = new HashMap<>();

        for (CourseSessionDetails.Session session : CourseSessionDetails.getSessionsList()) {
            Field field = null;
            try {
                field = CoachingCourseEntity.class.getField(session.getDbFieldName());
                Object value = field.get(course);
                if (Objects.nonNull(value)) {
                    String valueStr;
                    if (session.getDisplayName().equals(TEACHER_STUDENT_RATIO.getValue())) {
                        valueStr = String.valueOf(value);
                    } else {
                        valueStr = ((Integer) value).toString();
                    }
                    if (!StringUtils.isEmpty(valueStr)) {
                        courseMoreInfoMap.put(session.getDisplayName(), valueStr);
                    }
                }
            } catch (final Exception ex) {
                log.error("Got exception, course: {}, field: {}, exception: ", ex, course, field);
            }
        }
        return courseMoreInfoMap;
    }

    List<CoachingCourseData> getTopCoachingCoursesForExamId(Long examId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(EXAM_IDS, Collections.singletonList(examId));

        return (List<CoachingCourseData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.COACHING_COURSE, null,
                        TOP_ELEMENTS_ANY_PAGE_LIMIT);
    }

    List<CoachingCourseData> getTopCoachingCoursesForStreamId(Long streamId) {
        Map<String, List<Object>> filter = new HashMap<>();
        filter.put(STREAM_IDS, Collections.singletonList(streamId));

        return (List<CoachingCourseData>) (List<?>) searchDataHelper
                .getTopSearchData(filter, EducationEntity.COACHING_COURSE, null,
                        TOP_ELEMENTS_ANY_PAGE_LIMIT);
    }

    private List<String> fetchSections() {
        final Map<String, Object> propertyMap = propertyReader.getPropertiesAsMapByKey(
                DETAILS_PROPERTY_COMPONENT, DETAILS_PROPERTY_NAMESPACE, DETAILS_PROPERTY_KEY);

        return (List<String>) propertyMap.getOrDefault(COURSE,
                Collections.EMPTY_LIST);
    }

    private String calculateDiscountPercentage(final double original, final double discounted) {
        if (ComparisonUtils.thresholdBasedDoublesComparison(original, discounted)) {
            return EMPTY_STRING;
        }

        int discount = (int) Math.round(((original - discounted) * 100) / original);
        return discount + "%";
    }

    private String fillTargetExam(final Map<String, List<Exam>> examTypeAndExamListMap) {
        if (CollectionUtils.isEmpty(examTypeAndExamListMap)
                || null == examTypeAndExamListMap.get(TARGET_EXAM)
                || CollectionUtils.isEmpty(examTypeAndExamListMap.get(TARGET_EXAM))) {
            return null;
        }
        return examTypeAndExamListMap.get(TARGET_EXAM).get(0).getExamShortName();
    }

    private String convertLanguageEnumListToTextList(String languageList) {
        if (StringUtils.isEmpty(languageList)) {
            return null;
        }
        String[] languageEnumList = languageList.split(",");
        List<String> languageTextList = new ArrayList<>();
        for (String language : languageEnumList) {
            Language languageEnum = Language.fromString(language.trim());
            if (Objects.nonNull(languageEnum)) {
                languageTextList.add(languageEnum.getText());
            }
        }
        return String.join(",", languageTextList);
    }
}
