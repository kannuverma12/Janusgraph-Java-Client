package com.paytm.digital.education.explore.service.impl;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CASTEGROUP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER_CATEGORIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SUBEXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_DEGREES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_CUTOFF_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_CUTOFF_CASTEGROUP;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.explore.enums.Gender.OTHERS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.response.builders.InstituteDetailResponseBuilder;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.helper.GenderAndCasteGroupHelper;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@AllArgsConstructor
public class InstituteDetailServiceImpl {

    private CommonMongoRepository          commonMongoRepository;
    private InstituteDetailResponseBuilder instituteDetailResponseBuilder;
    private LeadDetailHelper               leadDetailHelper;
    private SubscriptionDetailHelper       subscriptionDetailHelper;

    private static int EXAM_PREFIX_LENGTH   = EXAM_PREFIX.length();
    private static int COURSE_PREFIX_LENGTH = COURSE_PREFIX.length();

    private GenderAndCasteGroupHelper        genderAndCasteGroupHelper;
    private Map<String, Map<String, Object>> genderCategoryMap;

    @PostConstruct
    private void setGenderCategoryMap() {
        genderCategoryMap = genderAndCasteGroupHelper.getGenderAndCasteGroupMap();
    }

    public InstituteDetail getDetail(Long entityId, Long userId,
            String fieldGroup, List<String> fields) throws IOException, TimeoutException {
        // fields are not being supported currently. Part of discussion

        InstituteDetail instituteDetail = getinstituteDetail(entityId, fieldGroup);
        if (userId != null && userId > 0) {
            updateShortist(instituteDetail, INSTITUTE, userId);
            updateGetInTouch(instituteDetail, INSTITUTE, userId);
        }
        return instituteDetail;
    }

    @Cacheable(value = "institute_detail")
    public InstituteDetail getinstituteDetail(Long entityId, String fieldGroup)
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

        Institute institute =
                commonMongoRepository.getEntityByFields(INSTITUTE_ID, entityId, Institute.class,
                        instituteFields);
        if (institute != null) {
            Long parentInstitutionId = institute.getParentInstitution();
            String parentInstitutionName = null;
            if (parentInstitutionId != null) {
                Institute parentInstitution = commonMongoRepository
                        .getEntityByFields(INSTITUTE_ID, parentInstitutionId, Institute.class,
                                parentInstitutionFields);
                parentInstitutionName =
                        parentInstitution != null ? parentInstitution.getOfficialName() : null;
            }
            return processInstituteDetail(institute, entityId, courseFields, examFields,
                    parentInstitutionName);
        }
        throw new BadRequestException(INVALID_INSTITUTE_ID,
                INVALID_INSTITUTE_ID.getExternalMessage());
    }

    @Cacheable(value = "institutes")
    public List<Institute> getInstitutes(List<Long> entityIds, List<String> groupFields)
            throws IOException, TimeoutException {
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

        List<Institute> institutes =
                commonMongoRepository.getEntityFieldsByValuesIn(INSTITUTE_ID, entityIds, Institute.class,
                        instituteFields);

        if (!CollectionUtils.isEmpty(institutes)) {
            for (Institute institute : institutes) {
                Long parentInstitutionId = institute.getParentInstitution();
                String parentInstitutionName = null;
                if (parentInstitutionId != null) {
                    Institute parentInstitution = commonMongoRepository
                            .getEntityByFields(INSTITUTE_ID, parentInstitutionId, Institute.class,
                                    parentInstitutionFields);
                    parentInstitutionName =
                            parentInstitution != null ? parentInstitution.getOfficialName() : null;
                }
            }
            return institutes;
        }

        throw new BadRequestException(INVALID_INSTITUTE_ID,
                INVALID_INSTITUTE_ID.getExternalMessage());
    }

    private InstituteDetail processInstituteDetail(Institute institute, Long entityId,
            List<String> courseFields, List<String> examFields, String parentInstitutionName)
            throws IOException, TimeoutException {
        List<Course> courses = null;
        if (!CollectionUtils.isEmpty(courseFields)) {
            courses = commonMongoRepository
                    .getEntitiesByIdAndFields(INSTITUTE_ID, entityId, Course.class,
                            courseFields);
        }
        Map<String, Object> examData = getExamData(courses);
        Set<Long> examIds = (Set<Long>) examData.get(EXAM_ID);
        List<Exam> examList = null;
        if (!CollectionUtils.isEmpty(examFields) && !CollectionUtils.isEmpty(examIds)) {
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(SUBEXAM_ID, new ArrayList<>(examIds));
            queryObject.put(EXAM_ID, new ArrayList<>(examIds));
            examList = commonMongoRepository
                    .findAll(queryObject, Exam.class, examFields, OR);
        }
        return instituteDetailResponseBuilder
                .buildResponse(institute, courses, examList, examData, examIds,
                        parentInstitutionName);
    }

    private Map<String, Object> getExamData(List<Course> courses) {
        Map<Long, String> examDegrees = new HashMap<>();
        Map<Long, Map<Gender, String>> examGenders = new HashMap<>();
        Map<Long, Map<String, String>> examCasteGroup = new HashMap<>();
        Map<String, Object> genderMap = genderCategoryMap.get(GENDER);
        Map<String, Object> casteGroupMap = genderCategoryMap.get(CASTEGROUP);
        Set<Long> examIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(courses)) {
            courses.forEach(course -> {
                if (!CollectionUtils.isEmpty(course.getExamsAccepted())) {
                    course.getExamsAccepted().forEach(examId -> {
                        examDegrees.put(examId, StringUtils.join(course.getMasterDegree(), ','));
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
                                genders.put(gender, (String) genderMap.get(gender.toString()));
                            } else {
                                genders.put(OTHERS, (String) genderMap.get(OTHERS.toString()));
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
        Map<String, Object> examData = new HashMap<>();
        examData.put(EXAM_DEGREES, examDegrees);
        examData.put(EXAM_CUTOFF_GENDER, examGenders);
        examData.put(EXAM_CUTOFF_CASTEGROUP, examCasteGroup);
        examData.put(EXAM_ID, examIds);
        return examData;
    }

    private void updateShortist(InstituteDetail instituteDetail, EducationEntity educationEntity,
            Long userId) {
        List<Long> subscribedEntities = subscriptionDetailHelper
                .getSubscribedEntities(educationEntity, userId,
                        Arrays.asList(instituteDetail.getInstituteId()));
        if (!CollectionUtils.isEmpty(subscribedEntities)) {
            instituteDetail.setShortlisted(true);
        }
    }

    private void updateGetInTouch(InstituteDetail instituteDetail, EducationEntity educationEntity,
            Long userId) {
        List<Long> leadEntities = leadDetailHelper
                .getLeadEntities(educationEntity, userId,
                        Arrays.asList(instituteDetail.getInstituteId()));
        if (!CollectionUtils.isEmpty(leadEntities)) {
            instituteDetail.setGetInTouch(true);
        }
    }
}
