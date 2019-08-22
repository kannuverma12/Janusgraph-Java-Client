package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.db.dao.CoachingInstituteDAO;
import com.paytm.digital.education.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Service
public class CoachingInstituteService {

    private CoachingInstituteDAO coachingInstituteDAO;

    public CoachingInstitute create(CoachingInstituteDataRequest request) {

        CoachingInstitute coachingInstitute =
                CoachingInstitute.builder()
                        .brandName(request.getBrandName())
                        .aboutInstitute(request.getAboutInstitute())
                        .officialAddress(request.getAddress())
                        .coverImage(request.getCoverImage())
                        .logo(request.getLogo())
                        .streams(request.getStreamIds())
                        .exams(request.getExamIds())
                        .courseTypes(request.getCourseTypes())
                        .establishmentYear(request.getEstablishmentYear())
                        .brochure(request.getBrochureUrl())
                        .keyHighlights(request.getHighlights())
                        .faqs(request.getFaqs())
                        .build();
        coachingInstitute.setIsEnabled(request.getIsEnabled());
        coachingInstitute.setPriority(request.getPriority());
        return coachingInstituteDAO.save(coachingInstitute);
    }

    public CoachingInstitute update(CoachingInstituteDataRequest request) {

        CoachingInstitute existingIntitute =
                Optional.ofNullable(coachingInstituteDAO.findByInstituteId(request.getInstituteId()))
                        .orElseThrow(() -> new ResourceAccessException(
                                CoachingConstants.RESOURCE_NOT_PRESENT));

        CoachingInstitute toSave =
                CoachingInstitute.builder()
                        .id(existingIntitute.getId())
                        .instituteId(request.getInstituteId())
                        .brandName(request.getBrandName())
                        .aboutInstitute(request.getAboutInstitute())
                        .officialAddress(request.getAddress())
                        .coverImage(request.getCoverImage())
                        .logo(request.getLogo())
                        .streams(request.getStreamIds())
                        .exams(request.getExamIds())
                        .courseTypes(request.getCourseTypes())
                        .establishmentYear(request.getEstablishmentYear())
                        .brochure(request.getBrochureUrl())
                        .keyHighlights(request.getHighlights())
                        .faqs(request.getFaqs())
                        .build();
        toSave.setIsEnabled(request.getIsEnabled());
        toSave.setPriority(request.getPriority());

        return coachingInstituteDAO.save(toSave);
    }
}
