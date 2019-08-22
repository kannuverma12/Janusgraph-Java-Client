package com.paytm.digital.education.coaching.data.service;

import com.paytm.digital.education.coaching.database.entity.CoachingCenter;
import com.paytm.digital.education.coaching.database.repository.CoachingCenterRespository;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTER_NOT_FOUND_ERROR;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SUCCESS_MESSAGE;

@Service
@Deprecated
public class CoachingCenterService {
    @Autowired
    private CoachingCenterRespository coachingCenterRespository;

    private ResponseDto successResponse        = new ResponseDto(200, SUCCESS_MESSAGE);
    private ResponseDto centerNotFoundResponse = new ResponseDto(404, CENTER_NOT_FOUND_ERROR);

    public ResponseDto getCoachingCenterById(long centerId, Boolean active) {
        CoachingCenter coachingCenter =
                coachingCenterRespository.findCoachingCenterById(centerId, active);
        if (Objects.isNull(coachingCenter)) {
            return centerNotFoundResponse;
        }
        return coachingCenter;
    }
}
