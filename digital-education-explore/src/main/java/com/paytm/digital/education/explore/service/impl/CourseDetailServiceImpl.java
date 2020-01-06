package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.dto.detail.ImportantDate;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.response.dto.detail.CourseDetail;
import com.paytm.digital.education.explore.response.dto.detail.CourseFee;
import com.paytm.digital.education.explore.response.dto.detail.CourseInstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.utility.FieldsRetrievalUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamDatesHelper;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_CLASS;
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

@Service
@RequiredArgsConstructor
public class CourseDetailServiceImpl {

    private final CommonMongoRepository       commonMongoRepository;
    private final DerivedAttributesHelper     derivedAttributesHelper;
    private final PropertyReader              propertyReader;
    private final SimilarInstituteServiceImpl similarInstituteService;
    private final BannerDataHelper            bannerDataHelper;
    private final ExamDatesHelper             examDatesHelper;
    private final CommonEntityMongoDAO        commonEntityMongoDAO;

    @Value("${course.default.instances.for.date:1}")
    private Integer defaultNoOfInstances;

    /*
     ** Method to get the course details and institute details
     */
    @EduCache(cache = "course_detail")
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
                    commonEntityMongoDAO.getCourseById(entityId, courseQueryFields);
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
                instituteDetails = commonEntityMongoDAO
                        .getInstituteById(course.getInstitutionId(), instituteQueryFields);
            }
            return buildResponse(course, instituteDetails, course.getExamsAccepted(), client,
                    courseFees, institute, widgets, derivedAttributes, examAccepted);
        } else {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
    }

    private List<ExamDetail> getExamsAccepted(List<Long> examIds) {

        List<String> fields = new ArrayList<>();
        fields.add(EXAM_SHORT_NAME);
        fields.add(EXAM_FULL_NAME);
        fields.add(INSTANCES);
        fields.add(SUB_EXAMS);
        fields.add(EXAM_ID);
        List<Exam> exams = commonEntityMongoDAO.getExamsByIdsIn(examIds, fields);

        List<ExamDetail> examsAccepted = new ArrayList<>();

        for (Exam exam : exams) {
            ExamDetail examDetail = new ExamDetail();
            List<ImportantDate> importantDates = examDatesHelper.getImportantDates(exam, defaultNoOfInstances);
            examDetail.setExamId(exam.getExamId());
            examDetail.setUrlDisplayName(
                    CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
            if (!CollectionUtils.isEmpty(importantDates)) {
                examDetail.setImportantDates(importantDates);
            }
            examDetail.setExamFullName(exam.getExamFullName());
            examDetail.setExamShortName(exam.getExamShortName());
            examsAccepted.add(examDetail);
        }

        return examsAccepted;
    }

    /*
     ** Find all te exams for the course
     */
    private Exam getExamNames(List<Long> examIds) {
        List<String> examQueryFields = new ArrayList<>();
        examQueryFields.add(EXAM_SHORT_NAME);
        examQueryFields.add(EXAM_FULL_NAME);
        return commonEntityMongoDAO.getExamById(examIds.get(0), examQueryFields);
    }

    /*
     ** Build the response combining the course and institute details
     */
    private CourseDetail buildResponse(Course course, Institute institute,
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

    private CourseFee getCourseFee(
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
