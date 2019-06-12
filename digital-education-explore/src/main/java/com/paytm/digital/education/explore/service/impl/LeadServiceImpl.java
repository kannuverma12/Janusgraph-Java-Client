package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.BaseLeadResponse;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.database.repository.LeadRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.service.LeadService;
import com.paytm.digital.education.explore.service.external.LeadCareer360Service;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IS_ACCEPTING_APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;

@Service
@AllArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {
    private LeadRepository        leadRepository;
    private CommonMongoRepository commonMongoRepository;
    private LeadCareer360Service  leadCareer360Service;

    @Override
    public com.paytm.digital.education.explore.response.dto.common.Lead captureLead(Lead lead) {
        validateLeadRequest(lead);
        BaseLeadResponse c360LeadRespose = leadCareer360Service.sendLead(lead);
        com.paytm.digital.education.explore.response.dto.common.Lead leadResponse =
                buildResponse(lead, c360LeadRespose);
        leadRepository.upsertLead(lead);
        return leadResponse;
    }

    @Override
    public com.paytm.digital.education.explore.response.dto.common.Lead unfollowLead(Lead lead) {
        validateUnfollowRequest(lead);
        BaseLeadResponse c360LeadRespose = leadCareer360Service.sendUnfollow(lead);
        com.paytm.digital.education.explore.response.dto.common.Lead leadResponse =
                buildResponse(lead, c360LeadRespose);
        leadRepository.upsertLead(lead);
        return leadResponse;
    }

    private com.paytm.digital.education.explore.response.dto.common.Lead buildResponse(Lead lead,
            BaseLeadResponse thirdPartyResponse) {
        if (Objects.isNull(lead.getBaseLeadResponse())) {
            lead.setBaseLeadResponse(Arrays.asList(thirdPartyResponse));
        } else {
            lead.getBaseLeadResponse().add(thirdPartyResponse);
        }
        com.paytm.digital.education.explore.response.dto.common.Lead leadResponse =
                new com.paytm.digital.education.explore.response.dto.common.Lead();
        if (Objects.isNull(thirdPartyResponse.getInterested())) {
            leadResponse.setError(true);
        } else {
            lead.setInterested(thirdPartyResponse.getInterested());
            leadResponse.setInterested(thirdPartyResponse.getInterested());
        }
        return leadResponse;
    }

    private void validateUnfollowRequest(Lead lead) {
        if (Objects.isNull(lead.getEntityType())) {
            throw new BadRequestException(ErrorEnum.ENTITY_TYPE_IS_MANDATORY_FOR_UNFOLLOW,
                    ErrorEnum.ENTITY_TYPE_IS_MANDATORY_FOR_UNFOLLOW.getExternalMessage());
        }
        if (EducationEntity.COURSE.equals(lead.getEntityType())) {
            if (Objects.isNull(lead.getInstituteId())) {
                throw new BadRequestException(
                        ErrorEnum.INSTITUTE_ID_AND_ENTITY_IS_MANDATORY_FOR_UNFOLLOW,
                        ErrorEnum.INSTITUTE_ID_AND_ENTITY_IS_MANDATORY_FOR_UNFOLLOW
                                .getExternalMessage());
            }
        } else if (EducationEntity.EXAM.equals(lead.getEntityType())) {
            if (Objects.isNull(lead.getEntityId())) {
                throw new BadRequestException(ErrorEnum.ENTITY_ID_NOT_PRESENT,
                        ErrorEnum.ENTITY_ID_NOT_PRESENT.getExternalMessage());
            }
        } else {
            throw new BadRequestException(ErrorEnum.ACTION_NOT_SUPPORTED,
                    ErrorEnum.ACTION_NOT_SUPPORTED.getExternalMessage());

        }
        List<Lead> leads = leadRepository
                .fetchInterestedLeadByInstituteIdANdUserId(lead.getUserId(),
                        Arrays.asList(lead.getInstituteId()));
        if (CollectionUtils.isEmpty(leads)) {
            throw new BadRequestException(ErrorEnum.ACTION_NOT_SUPPORTED,
                    ErrorEnum.ACTION_NOT_SUPPORTED.getExternalMessage());
        }
        lead.setEntityId(leads.get(0).getEntityId());
    }

    private void validateLeadRequest(Lead lead) {
        if (EducationEntity.COURSE.equals(lead.getEntityType())) {
            validateCourseLead(lead);
        } else if (EducationEntity.EXAM.equals(lead.getEntityType())) {
            validateExamLead(lead);
        } else {
            throw new BadRequestException(ErrorEnum.ENTITY_NOT_SUPPORTED_FOR_LEAD,
                    ErrorEnum.ENTITY_NOT_SUPPORTED_FOR_LEAD.getExternalMessage());
        }
    }

    private void validateCourseLead(Lead lead) {
        List<String> fieldGroup =
                Arrays.asList(COURSE_ID, INSTITUTE_ID, IS_ACCEPTING_APPLICATION);
        if (CollectionUtils.isEmpty(lead.getStream())) {
            throw new BadRequestException(ErrorEnum.STREAM_IS_MANDATORY_FOR_COURSE_LEAD,
                    ErrorEnum.STREAM_IS_MANDATORY_FOR_COURSE_LEAD.getExternalMessage());
        }
        if (Objects.isNull(lead.getInstituteId())) {
            throw new BadRequestException(ErrorEnum.VALID_INSTITUTE_ID_FOR_COURSE_LEAD,
                    ErrorEnum.VALID_INSTITUTE_ID_FOR_COURSE_LEAD.getExternalMessage());
        }
        Course course = commonMongoRepository
                .getEntityByFields(COURSE_ID, lead.getEntityId(), Course.class, fieldGroup);
        if (Objects.isNull(course)) {
            throw new BadRequestException(ErrorEnum.INVALID_COURSE_ID,
                    ErrorEnum.INVALID_COURSE_ID.getExternalMessage());
        }
        if (lead.getInstituteId().compareTo(course.getInstitutionId()) != 0) {
            throw new BadRequestException(ErrorEnum.VALID_INSTITUTE_ID_FOR_COURSE_LEAD,
                    ErrorEnum.VALID_INSTITUTE_ID_FOR_COURSE_LEAD.getExternalMessage());
        }
        if (!course.isAcceptingApplication()) {
            throw new BadRequestException(ErrorEnum.COURSE_IS_NOT_ACCEPTING_APPLICATION,
                    ErrorEnum.COURSE_IS_NOT_ACCEPTING_APPLICATION.getExternalMessage());
        }
    }

    private void validateExamLead(Lead lead) {
        List<String> fieldGroup = Arrays.asList(EXAM_ID);
        Exam exam = commonMongoRepository
                .getEntityByFields(EXAM_ID, lead.getEntityId(), Exam.class, fieldGroup);
        if (Objects.isNull(exam)) {
            throw new BadRequestException(ErrorEnum.INVALID_EXAM_ID,
                    ErrorEnum.INVALID_EXAM_ID.getExternalMessage());
        }
    }
}
