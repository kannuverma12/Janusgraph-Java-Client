package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.TopRankerDTO;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TopRankerManagerService {

    private static final Logger log = LoggerFactory.getLogger(TopRankerManagerService.class);

    @Autowired
    private final ProducerTopRankerService producerTopRankerService;

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    @Autowired
    private ProducerCoachingCenterService coachingCenterService;

    @Autowired
    private ProducerTargetExamService producerTargetExamService;

    @Autowired
    private ProducerCoachingCourseService producerCoachingCourseService;


    public TopRankerDTO create(final TopRankerDataRequest request) {

        if (Objects.nonNull(request.getTopRankerId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getTopRankerId());
        }

        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present : " + request.getInstituteId());
        }

        CoachingCenterEntity existingCoachingCenter =
                coachingCenterService.findByCenterId(request.getCenterId());
        if (Objects.isNull(existingCoachingCenter)) {
            throw new InvalidRequestException("center id not present : " + request.getCenterId());
        }

        producerTargetExamService.isValidExamIds(Arrays.asList(request.getExamId()));
        producerCoachingCourseService.isValidCourseIds(request.getCourseStudied());

        return TopRankerDTO.builder()
                .topRankerId(producerTopRankerService.create(request).getTopRankerId())
                .build();
    }

    public TopRankerDTO update(final TopRankerDataRequest request) {

        Optional.ofNullable(request.getTopRankerId())
                .orElseThrow(() -> new InvalidRequestException("top ranker id should be present"));


        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present : " + request.getInstituteId());
        }

        CoachingCenterEntity existingCoachingCenter =
                coachingCenterService.findByCenterId(request.getCenterId());
        if (Objects.isNull(existingCoachingCenter)) {
            throw new InvalidRequestException("center not present : " + request.getCenterId());
        }

        producerTargetExamService.isValidExamIds(Arrays.asList(request.getExamId()));
        producerCoachingCourseService.isValidCourseIds(request.getCourseStudied());

        return TopRankerDTO.builder()
                .topRankerId(producerTopRankerService.update(request).getTopRankerId())
                .build();

    }
}
