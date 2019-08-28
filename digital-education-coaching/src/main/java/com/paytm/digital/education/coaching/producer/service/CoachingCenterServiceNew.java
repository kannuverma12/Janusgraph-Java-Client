package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.db.dao.CoachingCenterDAO;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Service
public class CoachingCenterServiceNew {

    @Autowired
    private CoachingCenterDAO coachingCenterDAO;

    public CoachingCenterEntity insertCoachingCenter(CoachingCenterDataRequest request) {
        //        CoachingInstituteEntity coachingInstitute =
        //                coachingInstituteRepositoryNew.findByInstituteId(request.getInstituteId());
        //
        //        if (Objects.isNull(coachingInstitute)) {
        //            // TODO : Add exception here
        //            return null;
        //        }

        CoachingCenterEntity coachingCenterEntity = CoachingCenterEntity.builder()
                .courseTypes(request.getCourseTypes())
                .officialAddress(request.getOfficialAddress())
                .officialName(request.getOfficialName())
                .instituteId(request.getInstituteId())
                .build();

        coachingCenterEntity.setIsEnabled(request.getIsEnabled());
        coachingCenterEntity.setPriority(request.getPriority());

        return coachingCenterDAO.save(coachingCenterEntity);
    }

    public CoachingCenterEntity updateCoachingCenter(CoachingCenterDataRequest request) {

        //raise exception if coaching center is not preset

        CoachingCenterEntity existingCenter =
                Optional.ofNullable(coachingCenterDAO.findByCenterId(request.getCenterId()))
                        .orElseThrow(() -> new ResourceAccessException(
                                CoachingConstants.RESOURCE_NOT_PRESENT));


        //        //        CoachingInstituteEntity coachingInstitute =
        //        //                coachingInstituteRepositoryNew.findByInstituteId(request.getInstituteId());
        //
        //        if (Objects.isNull(coachingInstitute)) {
        //            // TODO : Add exception here
        //            return null;
        //        }

        CoachingCenterEntity toSave = CoachingCenterEntity.builder()
                .id(existingCenter.getId())
                .centerId(existingCenter.getCenterId())
                .officialAddress(request.getOfficialAddress())
                .officialName(request.getOfficialName())
                .instituteId(request.getInstituteId())
                .courseTypes(request.getCourseTypes())
                .build();

        return coachingCenterDAO.save(toSave);
    }
}
