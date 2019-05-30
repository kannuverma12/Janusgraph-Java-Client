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
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IS_ACCEPTING_APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;

@Service
@AllArgsConstructor
public class LeadServiceImpl implements LeadService {
    private LeadRepository        leadRepository;
    private CommonMongoRepository commonMongoRepository;
    private LeadCareer360Service  leadCareer360Service;

    public void validateCourseLead(Lead lead) {
        List<String> fieldGroup = Arrays.asList(COURSE_ID, INSTITUTE_ID, IS_ACCEPTING_APPLICATION);
        if (Objects.isNull(lead.getInstituteId())) {
            throw new BadRequestException(ErrorEnum.VALID_INSTITUTE_ID_FOR_COURSE_LEAD,
                    ErrorEnum.VALID_INSTITUTE_ID_FOR_COURSE_LEAD.getExternalMessage());
        }
        Course course = commonMongoRepository
                .getEntityByFields(COURSE_ID, lead.getEntityId(), Course.class, fieldGroup);
        System.out.println(course);
        if (Objects.isNull(course)) {
            throw new BadRequestException(ErrorEnum.INVALID_COURSE_ID,
                    ErrorEnum.INVALID_COURSE_ID.getExternalMessage());
        }
        if (lead.getInstituteId() != course.getInstitutionId()) {
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


    @Override
    public com.paytm.digital.education.explore.response.dto.common.Lead captureLead(
            @NotNull Lead lead) {
        if (EducationEntity.COURSE.equals(lead.getEntityType())) {
            validateCourseLead(lead);
        } else if (EducationEntity.EXAM.equals(lead.getEntityType())) {
            validateExamLead(lead);
        } else {
            throw new BadRequestException(ErrorEnum.ENTITY_NOT_SUPPORTED_FOR_LEAD,
                    ErrorEnum.ENTITY_NOT_SUPPORTED_FOR_LEAD.getExternalMessage());
        }
        BaseLeadResponse c360LeadRespose = leadCareer360Service.send(lead);
        com.paytm.digital.education.explore.response.dto.common.Lead leadResponse =
                new com.paytm.digital.education.explore.response.dto.common.Lead();
        leadResponse.setInterested(c360LeadRespose.getInterested());
        leadRepository.upsertLead(lead);
        return leadResponse;
    }
}
