package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.TopRankerDTO;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TopRankerManagerService {

    @Autowired
    private final TopRankerService topRankerService;

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    public TopRankerDTO create(final TopRankerDataRequest request) {

        if (Objects.nonNull(request.getTopRankerId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getTopRankerId());
        }

        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        return TopRankerDTO.builder().topRankerId(topRankerService.create(request).getTopRankerId())
                .build();
    }

    public TopRankerDTO update(final TopRankerDataRequest request) {

        Optional.ofNullable(request.getTopRankerId())
                .orElseThrow(() -> new InvalidRequestException("top ranker id should be present"));


        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        return TopRankerDTO.builder().topRankerId(topRankerService.update(request).getTopRankerId())
                .build();

    }
}
