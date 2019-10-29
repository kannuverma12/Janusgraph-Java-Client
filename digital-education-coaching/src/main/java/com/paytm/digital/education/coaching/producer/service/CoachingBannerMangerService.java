package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingBannerDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingBannerMangerService {

    @Autowired
    ProducerCoachingBannerService producerCoachingBannerService;

    public CoachingBannerDTO create(CoachingBannerDataRequest request) {
        if (Objects.nonNull(request.getCoachingBannerId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getCoachingBannerId());
        }
        return CoachingBannerDTO.builder()
                .coachingBannerId(producerCoachingBannerService.create(request).getCoachingBannerId())
                .build();
    }

    public CoachingBannerDTO update(CoachingBannerDataRequest request) {

        Optional.ofNullable(request.getCoachingBannerId())
                .orElseThrow(() -> new InvalidRequestException("banner id should be present"));

        return CoachingBannerDTO.builder()
                .coachingBannerId(producerCoachingBannerService.update(request).getCoachingBannerId())
                .build();
    }

}
