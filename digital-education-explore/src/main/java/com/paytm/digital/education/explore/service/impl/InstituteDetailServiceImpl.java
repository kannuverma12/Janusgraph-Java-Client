package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.Gender;
import com.paytm.digital.education.enums.PublishStatus;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.response.builders.InstituteDetailResponseBuilder;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.helper.GenderAndCasteGroupHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.constant.ExploreConstants.CASTEGROUP;
import static com.paytm.digital.education.constant.ExploreConstants.COURSE_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.EXAMS_ACCEPTED;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_CUTOFF_CASTEGROUP;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_CUTOFF_GENDER;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_DEGREES;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.GENDER;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.OTHER_CATEGORIES;
import static com.paytm.digital.education.constant.ExploreConstants.PARENT_INSTITUTION;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.constant.ExploreConstants.SUBEXAM_ID;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.enums.Gender.OTHERS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;


@Service
@AllArgsConstructor
public class InstituteDetailServiceImpl {

    private CommonMongoRepository          commonMongoRepository;
    private InstituteDetailResponseBuilder instituteDetailResponseBuilder;
    private SubscriptionDetailHelper       subscriptionDetailHelper;
    private CommonEntityMongoDAO           commonEntityMongoDAO;

    private static int EXAM_PREFIX_LENGTH   = EXAM_PREFIX.length();
    private static int COURSE_PREFIX_LENGTH = COURSE_PREFIX.length();

    private GenderAndCasteGroupHelper        genderAndCasteGroupHelper;
    private Map<String, Map<String, Object>> genderCategoryMap;


    @PostConstruct
    private void setGenderCategoryMap() {
        genderCategoryMap = genderAndCasteGroupHelper.getGenderAndCasteGroupMap();
    }

    @Cacheable(value = "institute_detail", keyGenerator = "customKeyGenerator")
    public InstituteDetail getinstituteDetail(Long entityId, String instituteUrlKey,
            String fieldGroup, Client client, boolean derivedAttributes,
            boolean cutOffs, boolean facilities, boolean gallery, boolean placements,
            boolean notableAlumni, boolean sections, boolean widgets, boolean coursesPerDegree,
            boolean campusEngagementFlag, boolean newsArticles)
            throws IOException, TimeoutException {
        List<String> groupFields =
                commonMongoRepository.getFieldsByGroup(Institute.class, fieldGroup);
        if (CollectionUtils.isEmpty(groupFields)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
        List<String> instituteFields = new ArrayList<>();
        List<String> courseFields = new ArrayList<>();
        List<String> examFields = new ArrayList<>();
        List<String> parentInstitutionFields = new ArrayList<>();

        parentInstitutionFields.add(OFFICIAL_NAME);

        for (String requestedField : groupFields) {
            if (requestedField.startsWith(COURSE_PREFIX)) {
                courseFields.add(requestedField
                        .substring(COURSE_PREFIX_LENGTH, requestedField.length()));
            } else if (requestedField.startsWith(EXAM_PREFIX)) {
                examFields
                        .add(requestedField.substring(EXAM_PREFIX_LENGTH, requestedField.length()));
            } else {
                instituteFields.add(requestedField);
            }
        }
        Map<String, Object> queryObject = new HashMap<>();
        List<Long> instituteIdList = new ArrayList<>();
        instituteIdList.add(entityId);
        queryObject.put(INSTITUTE_ID, instituteIdList);
        queryObject.put(PARENT_INSTITUTION, instituteIdList);
        List<Institute> institutes = commonEntityMongoDAO.getAllInstitutes(queryObject,
                instituteFields, OR);
        Institute institute = null;
        for (Institute college : institutes) {
            Long collegeId = college.getInstituteId();
            if (collegeId.equals(entityId)) {
                institute = college;
            } else {
                instituteIdList.add(collegeId);
            }
        }
        if (institute != null) {
            if (!instituteUrlKey
                    .equals(CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()))) {
                throw new BadRequestException(INVALID_INSTITUTE_NAME,
                        INVALID_INSTITUTE_NAME.getExternalMessage());
            }
            Long parentInstitutionId = institute.getParentInstitution();
            String parentInstitutionName = null;
            if (parentInstitutionId != null) {
                Institute parentInstitution = commonEntityMongoDAO
                        .getInstituteById(parentInstitutionId, parentInstitutionFields);
                parentInstitutionName =
                        parentInstitution != null ? parentInstitution.getOfficialName() : null;
            }
            return processInstituteDetail(institute, entityId, courseFields, examFields,
                    parentInstitutionName, instituteIdList, client, derivedAttributes, cutOffs,
                    facilities, gallery, placements, notableAlumni, sections, widgets,
                    coursesPerDegree, campusEngagementFlag, newsArticles);
        }
        throw new BadRequestException(INVALID_INSTITUTE_ID,
                INVALID_INSTITUTE_ID.getExternalMessage());
    }

    @Cacheable(value = "institutes", keyGenerator = "customKeyGenerator")
    public List<Institute> getInstitutes(List<Long> entityIds, List<String> groupFields)
            throws IOException, TimeoutException {
        if (CollectionUtils.isEmpty(groupFields)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
        List<String> instituteFields = new ArrayList<>();

        for (String requestedField : groupFields) {
            if (!requestedField.startsWith(COURSE_PREFIX) || !requestedField
                    .startsWith(EXAM_PREFIX)) {
                instituteFields.add(requestedField);
            }
        }

        Set<Long> searchIds = new HashSet<>(entityIds);
        List<Institute> institutes =
                commonEntityMongoDAO
                        .getInstitutesByIdsIn(new ArrayList<>(searchIds), instituteFields);

        if (!CollectionUtils.isEmpty(institutes) && searchIds.size() == institutes.size()) {
            return institutes;
        }

        throw new BadRequestException(INVALID_INSTITUTE_ID,
                INVALID_INSTITUTE_ID.getExternalMessage());
    }

    private InstituteDetail processInstituteDetail(Institute institute, Long entityId,
            List<String> courseFields, List<String> examFields, String parentInstitutionName,
            List<Long> instituteIdList, Client client, boolean derivedAttributes,
            boolean cutOffs, boolean facilities, boolean gallery, boolean placements,
            boolean notableAlumni, boolean sections, boolean widgets, boolean coursesPerDegree,
            boolean campusEngagementFlag, boolean newsArticles)
            throws IOException, TimeoutException {
        List<Course> courses = null;
        if (!CollectionUtils.isEmpty(courseFields)) {
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(INSTITUTE_ID, instituteIdList);
            courseFields.add(INSTITUTE_ID); // it will be removed in the future
            courseFields.add(STREAM_IDS);
            courseFields.add(EXAMS_ACCEPTED);
            /** TODO:-
             * InstituteIdList can have hundreds of items. This can becomes an issue when the cache key is
             * generated for findAll method in commonMongoRepository.
             * See:- https://jira.mypaytm.com/browse/EDU-5038.
             * One alternative is to use mongodb lookups to get all courses. This will
             * also save a query where we fetch all child institutes.
             * Connection reset issue was seen for institute id 100 (osmania university)
             */
            courses = commonEntityMongoDAO.getAllCourses(queryObject, courseFields, OR);
        }
        Map<String, Object> examData = getExamData(courses, entityId);
        Set<Long> examIds = (Set<Long>) examData.get(EXAM_ID);
        List<Exam> examList = null;
        if (!CollectionUtils.isEmpty(examFields) && !CollectionUtils.isEmpty(examIds)) {
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(SUBEXAM_ID, new ArrayList<>(examIds));
            queryObject.put(EXAM_ID, new ArrayList<>(examIds));
            examList = commonEntityMongoDAO.getAllExams(queryObject, examFields, OR);
        }
        return instituteDetailResponseBuilder
                .buildResponse(institute, courses, examList, examData, examIds,
                        parentInstitutionName, client, derivedAttributes, cutOffs, facilities,
                        gallery, placements, notableAlumni, sections, widgets, coursesPerDegree,
                        campusEngagementFlag, newsArticles);
    }

    private Map<String, Object> getExamData(List<Course> courses, Long instituteId) {
        Map<Long, String> examDegrees = new HashMap<>();
        Map<Long, Map<Gender, String>> examGenders = new HashMap<>();
        Map<Long, Map<String, String>> examCasteGroup = new HashMap<>();
        Map<String, Object> genderMap = genderCategoryMap.get(GENDER);
        Map<String, Object> casteGroupMap = genderCategoryMap.get(CASTEGROUP);
        Set<Long> examIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(courses)) {
            //exclude child institute courses for the cutoffs
            courses.stream()
                    .filter(course -> course.getInstitutionId().equals(instituteId)
                            && course.getPublishingStatus() == PublishStatus.PUBLISHED)
                    .forEach(course -> {
                        if (!CollectionUtils.isEmpty(course.getExamsAccepted())) {
                            course.getExamsAccepted().forEach(examId -> {
                                examDegrees.put(examId,
                                        StringUtils.join(course.getMasterDegree(), ','));
                                examIds.add(examId);
                            });
                            if (!CollectionUtils.isEmpty(course.getCutoffs())) {
                                course.getCutoffs().forEach(cutoff -> {
                                    long examId = cutoff.getExamId();
                                    Gender gender = cutoff.getGender();
                                    Map<Gender, String> genders = examGenders.get(examId);
                                    if (Objects.isNull(genders)) {
                                        genders = new HashMap<>();
                                    }
                                    if (Objects.nonNull(gender)) {
                                        genders.put(gender,
                                                (String) genderMap.get(gender.toString()));
                                    } else {
                                        genders.put(OTHERS,
                                                (String) genderMap.get(OTHERS.toString()));
                                    }
                                    examGenders.put(examId, genders);

                                    Map<String, String> casteGroups = examCasteGroup.get(examId);
                                    String caste = cutoff.getCasteGroup();
                                    if (Objects.isNull(casteGroups)) {
                                        casteGroups = new HashMap<>();
                                    }
                                    if (Objects.nonNull(caste)) {
                                        casteGroups.put(caste, (String) casteGroupMap.get(caste));
                                    } else {
                                        casteGroups.put(OTHER_CATEGORIES,
                                                (String) casteGroupMap.get(OTHER_CATEGORIES));
                                    }
                                    examCasteGroup.put(examId, casteGroups);
                                    examIds.add(examId);
                                });
                            }
                        }
                    });
        }

        List<Exam> exams = commonMongoRepository
                .getEntityFieldsByValuesIn(EXAM_ID, new ArrayList<>(examIds), Exam.class,
                        Collections.singletonList(EXAM_ID));
        Set<Long> examIdsInDb = Optional.ofNullable(exams).orElse(new ArrayList<>()).stream()
                .map(Exam::getExamId)
                .collect(Collectors.toSet());
        Map<String, Object> examData = new HashMap<>();
        examData.put(EXAM_DEGREES, examDegrees);
        examData.put(EXAM_CUTOFF_GENDER, examGenders);
        examData.put(EXAM_CUTOFF_CASTEGROUP, examCasteGroup);
        examData.put(EXAM_ID, examIdsInDb);
        return examData;
    }

    public void updateShortist(InstituteDetail instituteDetail, EducationEntity educationEntity,
            Long userId, Client client) {

        List<Long> instituteIds = new ArrayList<>();
        instituteIds.add(instituteDetail.getInstituteId());
        if (Client.APP.equals(client) && !CollectionUtils.isEmpty(instituteDetail.getWidgets())) {
            for (Widget widget : instituteDetail.getWidgets()) {
                if (INSTITUTE.name().equals(widget.getEntity())) {
                    for (WidgetData widgetData : widget.getData()) {
                        instituteIds.add(widgetData.getEntityId());
                    }
                }
            }
        }
        List<Long> subscribedEntities = subscriptionDetailHelper
                .getSubscribedEntities(educationEntity, userId, instituteIds);
        if (!CollectionUtils.isEmpty(subscribedEntities)) {
            if (subscribedEntities.contains(instituteDetail.getInstituteId())) {
                instituteDetail.setShortlisted(true);
            }
            if (Client.APP.equals(client) && !CollectionUtils.isEmpty(instituteDetail.getWidgets())) {
                for (Widget widget : instituteDetail.getWidgets()) {
                    if (INSTITUTE.name().equals(widget.getEntity())) {
                        for (WidgetData widgetData : widget.getData()) {
                            if (subscribedEntities.contains(widgetData.getEntityId())) {
                                widgetData.setShortlisted(true);
                            }
                        }
                    }
                }
            }
        }
    }

}
