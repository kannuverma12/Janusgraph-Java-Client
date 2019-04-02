package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.builders.InstituteDetailResponseBuilder;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
            return processInstituteDetail(institute, entityId, courseFields, examFields);
        }
        throw new BadRequestException(INVALID_INSTITUTE_ID,
                INVALID_INSTITUTE_ID.getExternalMessage());
    }

    private InstituteDetail processInstituteDetail(Institute institute, Long entityId,
            List<String> courseFields, List<String> examFields)
            throws IOException, TimeoutException {
        List<Course> courses = null;
        if (!CollectionUtils.isEmpty(courseFields)) {
            courses = commonMongoRepository
                    .getEntitiesByIdAndFields(INSTITUTE_ID, entityId, Course.class,
                            courseFields);
        }
        Set<Long> examIds = getExamIds(courses);
        List<Exam> examList = null;
        if (!CollectionUtils.isEmpty(examFields) && !CollectionUtils.isEmpty(examIds)) {
            examList = commonMongoRepository
                    .getEntityFieldsByValuesIn(EXAM_ID, new ArrayList<>(examIds), Exam.class,
                            examFields);
        }
        return instituteDetailResponseBuilder.buildResponse(institute, courses, examList);
    }

    private Set<Long> getExamIds(List<Course> courses) {
        Set<Long> examIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(courses)) {
            courses.forEach(course -> {
                if (!CollectionUtils.isEmpty(course.getExamsAccepted())) {
                    examIds.addAll(course.getExamsAccepted());
                }
            });
        }
        return examIds;
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
