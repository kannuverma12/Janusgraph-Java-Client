package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CoachingCourseManagerService {

    @Autowired
    private CoachingCourseAdminService programService;

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    @Autowired
    private StreamService streamService;


    public CoachingCourseDTO save(CoachingCourseDataRequest coachingCourseDataRequest) {

        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService
                        .findByInstituteId(coachingCourseDataRequest.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        List<Long> existingStreamIds =
                streamService.findAllByStreamId(coachingCourseDataRequest.getStreamIds())
                        .stream().map(StreamEntity::getStreamId).collect(Collectors.toList());
        if (coachingCourseDataRequest.getStreamIds().stream()
                .filter(id -> !existingStreamIds.contains(id)).count() > 0) {
            throw new InvalidRequestException("StreamEntity ids not present");
        }

        //TODO : add exam validation

        return CoachingCourseDTO
                .builder().courseId(programService.save(coachingCourseDataRequest).getCourseId())
                .build();
    }

    public CoachingCourseDTO update(CoachingCourseDataRequest coachingCourseDataRequest) {

        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService
                        .findByInstituteId(coachingCourseDataRequest.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        List<Long> existingStreamIds =
                streamService.findAllByStreamId(coachingCourseDataRequest.getStreamIds())
                        .stream().map(StreamEntity::getStreamId).collect(Collectors.toList());
        if (coachingCourseDataRequest.getStreamIds().stream()
                .filter(id -> !existingStreamIds.contains(id)).count() > 0) {
            throw new InvalidRequestException("StreamEntity ids not present");
        }

        //TODO : add exam validation

        return CoachingCourseDTO
                .builder()
                .courseId(programService.update(coachingCourseDataRequest).getCourseId()).build();
    }
}
