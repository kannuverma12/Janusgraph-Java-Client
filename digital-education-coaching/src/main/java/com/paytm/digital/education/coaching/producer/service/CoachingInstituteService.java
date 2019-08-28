package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.db.dao.CoachingInstituteDAO;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Service
public class CoachingInstituteService {

    private CoachingInstituteDAO coachingInstituteDAO;

    public CoachingInstituteEntity create(CoachingInstituteDataRequest request) {

        CoachingInstituteEntity coachingInstituteEntity =
                CoachingInstituteEntity.builder()
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
        coachingInstituteEntity.setIsEnabled(request.getIsEnabled());
        coachingInstituteEntity.setPriority(request.getPriority());
        return coachingInstituteDAO.save(coachingInstituteEntity);
    }

    public CoachingInstituteEntity update(CoachingInstituteDataRequest request) {

        CoachingInstituteEntity existingIntitute =
                coachingInstituteDAO.findByInstituteId(request.getInstituteId())
                        .orElseThrow(() -> new ResourceAccessException(
                                CoachingConstants.RESOURCE_NOT_PRESENT));

        CoachingInstituteEntity toSave =
                CoachingInstituteEntity.builder()
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
