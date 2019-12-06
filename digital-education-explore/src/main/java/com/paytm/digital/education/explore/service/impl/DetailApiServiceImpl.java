package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.enums.EducationEntity.SCHOOL;

import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CourseDetail;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.service.SchoolDetailService;
import com.paytm.digital.education.explore.service.SchoolService;
import com.paytm.digital.education.explore.service.helper.CTAHelper;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class DetailApiServiceImpl implements SchoolDetailService {

    private final InstituteDetailServiceImpl instituteDetailService;
    private final ExamDetailServiceImpl      examDetailService;
    private final CourseDetailServiceImpl    courseDetailService;
    private final LeadDetailHelper           leadDetailHelper;
    private final SubscriptionDetailHelper   subscriptionDetailHelper;
    private final SchoolService              schoolService;
    private final CTAHelper                  ctaHelper;

    public ExamDetail getExamDetail(Long entityId, String examUrlKey, Long userId,
            String fieldGroup, List<String> fields, Client client, boolean syllabus,
            boolean importantDates, boolean derivedAttributes, boolean examCenters,
            boolean sections, boolean widgets,
            boolean policies) {
        // fields are not being supported currently. Part of discussion

        ExamDetail examDetail = examDetailService.getExamDetail(entityId, examUrlKey, fieldGroup, fields, client,
                syllabus, importantDates, derivedAttributes, examCenters, sections, widgets, policies);
        if (userId != null && userId > 0) {
            examDetail.setInterested(isInterested(EXAM, examDetail.getExamId(), userId));
            examDetail.setShortlisted(isShortlisted(examDetail.getExamId(), userId, EXAM));
        }
        List<CTA> ctas = ctaHelper.buildCTA(examDetail, client);

        if (!CollectionUtils.isEmpty(ctas)) {
            examDetail.setCtaList(ctas);
        }
        return examDetail;
    }

    public InstituteDetail getinstituteDetail(Long entityId, String instituteUrlKey, Long userId,
            String fieldGroup, List<String> fields, Client client, boolean derivedAttributes,
            boolean cutOffs, boolean facilities, boolean gallery, boolean placements,
            boolean notableAlumni, boolean sections, boolean widgets, boolean coursesPerDegree,
            boolean campusEngagementFlag)
            throws IOException, TimeoutException {
        // fields are not being supported currently. Part of discussion
        InstituteDetail instituteDetail = instituteDetailService.getinstituteDetail(entityId, instituteUrlKey,
                fieldGroup, client, derivedAttributes, cutOffs, facilities, gallery, placements,
                notableAlumni, sections, widgets, coursesPerDegree, campusEngagementFlag);
        if (userId != null && userId > 0) {
            instituteDetailService.updateShortist(instituteDetail, INSTITUTE, userId, client);
            instituteDetail.setInterested(isInterestedInInstitute(instituteDetail.getInstituteId(), userId));
        }
        List<CTA> ctaList = ctaHelper.buildCTA(instituteDetail, client);
        if (!CollectionUtils.isEmpty(ctaList)) {
            instituteDetail.setCtaList(ctaList);
        }
        return instituteDetail;
    }

    public CourseDetail getCourseDetail(Long entityId, String courseUrlKey, Long userId,
            String fieldGroup, List<String> fields, Client client, boolean courseFees,
            boolean institute, boolean widgets, boolean derivedAttributes, boolean examAccepted) {
        CourseDetail courseDetail =
                courseDetailService.getCourseDetails(entityId, courseUrlKey, fieldGroup, fields, client,
                        courseFees, institute, widgets, derivedAttributes, examAccepted);
        if (userId != null && userId > 0) {
            courseDetail.getInstitute().setInterested(isInterestedInInstitute(courseDetail.getInstituteId(), userId));
        }
        return courseDetail;
    }

    private boolean isInterested(EducationEntity educationEntity, Long entityId, Long userId) {
        List<Long> leadEntities = leadDetailHelper
                .getInterestedLeadByEntity(educationEntity, userId, entityId);
        return (!CollectionUtils.isEmpty(leadEntities));
    }

    private boolean isInterestedInInstitute(Long instituteId,
            Long userId) {
        List<Long> leadEntities = leadDetailHelper
                .getInterestedLeadInstituteIds(userId,
                        Arrays.asList(instituteId));
        return (!CollectionUtils.isEmpty(leadEntities));
    }

    private boolean isShortlisted(Long entityId,
            Long userId, EducationEntity educationEntity) {
        List<Long> examIds = new ArrayList<>();
        examIds.add(entityId);

        List<Long> subscribedEntities = subscriptionDetailHelper
                .getSubscribedEntities(educationEntity, userId, examIds);
        return (!CollectionUtils.isEmpty(subscribedEntities));
    }

    @Override
    public SchoolDetail getSchoolDetails(
            Long schoolId, Client client, String schoolName,
            List<String> fields, String fieldGroup, Long userId) {
        SchoolDetail schoolDetail = schoolService.getSchoolDetails(
            schoolId, client, schoolName, fields, fieldGroup);
        if (Objects.nonNull(userId) && userId > 0) {
            updateShortList(schoolDetail, SCHOOL, userId);
        }
        return schoolDetail;
    }

    private void updateShortList(SchoolDetail schoolDetail, EducationEntity educationEntity,
                                 Long userId) {
        schoolDetail.setShortlisted(isShortlisted(schoolDetail.getSchoolId(), userId, educationEntity));
    }
}
