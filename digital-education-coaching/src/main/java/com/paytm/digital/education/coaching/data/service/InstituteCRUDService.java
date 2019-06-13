package com.paytm.digital.education.coaching.data.service;

import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.response.dto.InstituteResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InstituteCRUDService {

    private CoachingInstituteRepository coachingInstituteRepository;

    public InstituteResponseDto createInstitute(CoachingInstitute coachingInstitute) {
        return (InstituteResponseDto) coachingInstituteRepository.createCoaching(coachingInstitute);
    }

    public InstituteResponseDto updateInstitute(CoachingInstitute coachingInstitute) {
        return (InstituteResponseDto) coachingInstituteRepository.updateCoaching(coachingInstitute);
    }

    public long updateInstituteStatus(long instituteId, boolean activate) {
        return coachingInstituteRepository.updateCoachingStatus(instituteId, activate);
    }

    public InstituteResponseDto getInstituteById(long instituteId, boolean active) {
        return (InstituteResponseDto) coachingInstituteRepository.findCoachingById(instituteId, active);
    }

    public long updateCoachingCenterStatus(long instituteId, long centerId, boolean activate) {
        return coachingInstituteRepository.updateCoachingCenterStatus(instituteId, centerId, activate);
    }


}
