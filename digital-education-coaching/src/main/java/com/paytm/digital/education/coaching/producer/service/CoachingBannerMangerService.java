package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingBannerDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CoachingBannerMangerService {

    @Autowired
    CoachingBannerService coachingBannerService;

    public CoachingBannerDTO create(CoachingBannerDataRequest request) {
        if (Objects.nonNull(request.getCoachingBannerId())) {
            throw new InvalidRequestException("Coaching Banner ID should be null in post request");
        }
        return CoachingBannerDTO.builder()
                .coachingBannerId(coachingBannerService.create(request).getCoachingBannerId())
                .build();
    }

    public CoachingBannerDTO update(CoachingBannerDataRequest request) {
        return CoachingBannerDTO.builder()
                .coachingBannerId(coachingBannerService.update(request).getCoachingBannerId())
                .build();
    }

}
