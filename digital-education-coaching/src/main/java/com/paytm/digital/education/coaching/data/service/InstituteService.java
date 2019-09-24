package com.paytm.digital.education.coaching.data.service;

import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_NOT_FOUND_ERROR;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SUCCESS_MESSAGE;

@Service
@AllArgsConstructor
public class InstituteService {

    private static ResponseDto instituteNotFoundResponse =
            new ResponseDto(404, COACHING_NOT_FOUND_ERROR);
    private static ResponseDto successResponse           =
            new ResponseDto(200, SUCCESS_MESSAGE);
    private CoachingInstituteRepository coachingInstituteRepository;

    public ResponseDto createInstitute(CoachingInstitute coachingInstitute) {
        return coachingInstituteRepository.createCoaching(coachingInstitute);
    }

    public ResponseDto updateInstitute(CoachingInstitute coachingInstitute) {
        return coachingInstituteRepository.updateCoaching(coachingInstitute);
    }

    public ResponseDto updateInstituteStatus(long instituteId, boolean activate) {
        coachingInstituteRepository.updateCoachingStatus(instituteId, activate);
        return successResponse;
    }

    public ResponseDto getInstituteById(long instituteId, Boolean active) {
        CoachingInstitute coachingInstitute =
                coachingInstituteRepository.findCoachingById(instituteId, active);
        if (Objects.isNull(coachingInstitute)) {
            return instituteNotFoundResponse;
        }
        return coachingInstitute;
    }

    public ResponseDto updateCoachingCenterStatus(long instituteId, long centerId,
            boolean activate) {
        coachingInstituteRepository.updateCoachingCenterStatus(instituteId, centerId, activate);
        return successResponse;
    }


}
