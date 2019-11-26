package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.dto.detail.Event;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.response.dto.detail.CourseDetail;
import com.paytm.digital.education.explore.response.dto.detail.CourseFee;
import com.paytm.digital.education.explore.response.dto.detail.CourseInstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.utility.FieldsRetrievalUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_CLASS;
import static com.paytm.digital.education.constant.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.INSTANCES;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_EXAMS;
import static com.paytm.digital.education.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_COURSE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_COURSE_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;

@AllArgsConstructor
@Service
public class CourseDetailServiceImpl {

    private CommonMongoRepository       commonMongoRepository;
    private DerivedAttributesHelper     derivedAttributesHelper;
    private PropertyReader              propertyReader;
    private SimilarInstituteServiceImpl similarInstituteService;
    private BannerDataHelper            bannerDataHelper;
    private LeadDetailHelper            leadDetailHelper;
    private ExamInstanceHelper          examInstanceHelper;

    @Cacheable(value = "course_detail_service", keyGenerator = "customKeyGenerator")
    public CourseDetail getDetail(Long entityId, String courseUrlKey, Long userId,
            String fieldGroup, List<String> fields, Client client, boolean courseFees,
            boolean institute, boolean widgets, boolean derivedAttributes, boolean examAccepted) {
        CourseDetail courseDetail =
                getCourseDetails(entityId, courseUrlKey, fieldGroup, fields, client,
                        courseFees, institute, widgets, derivedAttributes, examAccepted);
        if (userId != null && userId > 0) {
            updateInterested(courseDetail, userId);
        }
        return courseDetail;
    }

    /*
     ** Method to get the course details and institute details
     */
    @Cacheable(value = "course_detail", keyGenerator = "customKeyGenerator")
    public CourseDetail getCourseDetails(long entityId, String courseUrlKey, String fieldGroup,
            List<String> fields, Client client, boolean courseFees,
            boolean institute, boolean widgets, boolean derivedAttributes, boolean examAccepted) {
        List<String> queryFields = null;
        if (StringUtils.isNotBlank(fieldGroup)) {
            queryFields = commonMongoRepository.getFieldsByGroup(Course.class, fieldGroup);
        } else {
            queryFields = fields;
        }
        if (!CollectionUtils.isEmpty(queryFields)) {
            Map<String, ArrayList<String>> allFields =
                    FieldsRetrievalUtil.getFormattedFields(queryFields, COURSE_CLASS);
            ArrayList<String> courseQueryFields = allFields.get(COURSE.name().toLowerCase());
            courseQueryFields.add(INSTITUTE_ID);
            Course course =
                    commonMongoRepository.getEntityByFields(COURSE_ID, entityId, Course.class,
                            courseQueryFields);
            ArrayList<String> instituteQueryFields = allFields.get(INSTITUTE.name().toLowerCase());
            if (course == null) {
                throw new BadRequestException(INVALID_COURSE_ID,
                        INVALID_COURSE_ID.getExternalMessage());
            }
            if (!courseUrlKey.equals(CommonUtil
                    .convertNameToUrlDisplayName(course.getCourseNameOfficial()))) {
                throw new BadRequestException(INVALID_COURSE_NAME,
                        INVALID_COURSE_NAME.getExternalMessage());
            }
            Institute instituteDetails = null;
            if (course.getInstitutionId() != null && !CollectionUtils
                    .isEmpty(instituteQueryFields)) {
                instituteDetails = commonMongoRepository.getEntityByFields(INSTITUTE_ID,
                        course.getInstitutionId(),
                        Institute.class,
                        instituteQueryFields);
            }
            return buildResponse(course, instituteDetails, course.getExamsAccepted(), client,
                    courseFees, institute, widgets, derivedAttributes, examAccepted);
        } else {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
    }

    @Cacheable(value = "course_accepted_exams", keyGenerator = "customKeyGenerator")
    public List<ExamDetail> getExamsAccepted(List<Long> examIds) {

        List<String> fields = new ArrayList<>();
        fields.add(EXAM_SHORT_NAME);
        fields.add(EXAM_FULL_NAME);
        fields.add(INSTANCES);
        fields.add(SUB_EXAMS);
        fields.add(EXAM_ID);
        List<Exam> exams = commonMongoRepository
                .getEntityFieldsByValuesIn(EXAM_ID, examIds, Exam.class, fields);

        List<ExamDetail> examsAccepted = new ArrayList<>();

        for (Exam exam : exams) {
            ExamDetail examDetail = new ExamDetail();
            List<Event> impDates = examInstanceHelper.getImportantDates(exam);
            examDetail.setExamId(exam.getExamId());
            examDetail.setUrlDisplayName(
                    CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
            examDetail.setImportantDates(impDates);
            examDetail.setExamFullName(exam.getExamFullName());
            examDetail.setExamShortName(exam.getExamShortName());
            examsAccepted.add(examDetail);
        }

        return examsAccepted;
    }


    /*
     ** Find all te exams for the course
     */
    @Cacheable(value = "course_exam_names", keyGenerator = "customKeyGenerator")
    public Exam getExamNames(List<Long> examIds) {
        List<String> examQueryFields = new ArrayList<>();
        examQueryFields.add(EXAM_SHORT_NAME);
        examQueryFields.add(EXAM_FULL_NAME);
        return commonMongoRepository.getEntityByFields(EXAM_ID,
                examIds.get(0),
                Exam.class,
                examQueryFields);
    }

    /*
     ** Build the response combining the course and institute details
     */
    @Cacheable(value = "course_detail_build_response", keyGenerator = "customKeyGenerator")
    public CourseDetail buildResponse(Course course, Institute institute,
            List<Long> examIds, Client client, boolean courseFees,
            boolean instituteFlag, boolean widgets, boolean derivedAttributes, boolean examAccepted) {
        CourseDetail courseDetail = new CourseDetail();
        courseDetail.setCourseId(course.getCourseId());
        courseDetail.setAboutCourse(course.getAboutCourse());
        courseDetail.setCourseNameOfficial(course.getCourseNameOfficial());
        courseDetail.setCourseDuration(course.getCourseDuration());
        courseDetail.setSeatsAvailable(course.getSeatsAvailable());
        courseDetail.setStudyMode(course.getStudyMode());
        if (courseFees) {
            courseDetail.setCourseFees(getAllCourseFees(course.getCourseFees()));
        }
        courseDetail.setFeesUrlOfficial(course.getFeesUrlOfficial());
        courseDetail.setAdmissionProcess(course.getAdmissionProcess());
        courseDetail.setAdmissionProcessUrlOfficial(course.getAdmissionProcessUrlOfficial());
        courseDetail.setEligibilityCriteria(course.getEligibilityCriteria());
        courseDetail.setEligibilityUrlOfficial(course.getEligibilityUrlOfficial());
        courseDetail.setInstituteId(course.getInstitutionId());
        courseDetail.setOfficialBrochureUrl(course.getOfficialBrochureUrl());
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(COURSE.name().toLowerCase(), course);
        if (!CollectionUtils.isEmpty(examIds)) {
            if (Client.APP.equals(client)) {
                if (examAccepted) {
                    courseDetail.setExamsAccepted(getExamsAccepted(examIds));
                }
            } else {
                highlights.put(EXAM.name().toLowerCase(), getExamNames(examIds));
            }
        }
        if (derivedAttributes) {
            courseDetail.setDerivedAttributes(derivedAttributesHelper
                    .getDerivedAttributes(highlights, COURSE.name().toLowerCase(), client));
        }
        if (widgets) {
            courseDetail.setWidgets(similarInstituteService.getSimilarInstitutes(institute));
        }
        courseDetail.setBanners(bannerDataHelper.getBannerData(COURSE.name().toLowerCase(), null));
        if (institute != null && instituteFlag) {
            CourseInstituteDetail courseInstituteDetail = new CourseInstituteDetail();
            courseInstituteDetail.setOfficialName(institute.getOfficialName());
            courseInstituteDetail.setUrlDisplayName(
                    CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()));
            courseInstituteDetail.setOfficialAddress(
                    CommonUtil.getOfficialAddress(institute.getInstitutionState(),
                            institute.getInstitutionCity(), institute.getPhone(),
                            institute.getUrl(),
                            institute.getOfficialAddress()));
            if (institute.getIsClient() == 1) {
                courseInstituteDetail.setIsClient(true);
            } else {
                courseInstituteDetail.setIsClient(false);
            }
            courseDetail.setInstitute(courseInstituteDetail);
        }
        return courseDetail;
    }

    @Cacheable(value = "course_interested", key = "'course_interested' + #courseDetail.instituteId "
            + "+ #userId")
    public void updateInterested(CourseDetail courseDetail, Long userId) {
        List<Long> leadEntities = leadDetailHelper
                .getInterestedLeadInstituteIds(userId,
                        Arrays.asList(courseDetail.getInstituteId()));
        if (!CollectionUtils.isEmpty(leadEntities)) {
            courseDetail.getInstitute().setInterested(true);
        }
    }

    private List<CourseFee> getAllCourseFees(
            List<com.paytm.digital.education.database.entity.CourseFee> courseFee) {
        ArrayList<CourseFee> allCourseFees = new ArrayList<>();
        if (courseFee != null) {
            for (com.paytm.digital.education.database.entity.CourseFee fee : courseFee) {
                allCourseFees.add(getCourseFee(fee));
            }
            return allCourseFees;
        }
        return null;
    }

    @Cacheable(value = "course_fee", key = "'course_fee' + #fee.fee + #fee.casteGroup")
    public CourseFee getCourseFee(
            com.paytm.digital.education.database.entity.CourseFee fee) {
        Map<String, Object> cutoffDisplayNames = propertyReader.getPropertiesAsMapByKey(
                ExploreConstants.EXPLORE_COMPONENT, ExploreConstants.COURSE_DETAIL,
                ExploreConstants.CASTEGROUP);
        String displayName = fee.getCasteGroup();
        if (cutoffDisplayNames.containsKey(fee.getCasteGroup())) {
            displayName = (String) cutoffDisplayNames.get(fee.getCasteGroup());
        }
        return CourseFee.builder().casteGroup(fee.getCasteGroup())
                .fee(fee.getFee()).casteGroupDisplay(displayName).build();
    }
}
