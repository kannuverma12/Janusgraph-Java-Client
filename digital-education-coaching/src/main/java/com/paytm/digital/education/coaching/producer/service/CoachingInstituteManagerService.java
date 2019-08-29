package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingInstituteDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachingInstituteManagerService {

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    @Autowired
    private StreamService streamService;

    public CoachingInstituteDTO create(CoachingInstituteDataRequest request) {

        List<Long> existingStreamIds = streamService.findAllByStreamId(request.getStreamIds())
                .stream().map(StreamEntity::getStreamId).collect(Collectors.toList());
        if (request.getStreamIds().stream()
                .filter(id -> !existingStreamIds.contains(id)).count() > 0) {
            throw new InvalidRequestException("StreamEntity ids not present");
        }

        //TODO : add exam validation

        return CoachingInstituteDTO.builder()
                .instituteId(coachingInstituteService.create(request).getInstituteId()).build();
    }

    public CoachingInstituteDTO update(CoachingInstituteDataRequest request) {

        List<Long> existingStreamIds = streamService.findAllByStreamId(request.getStreamIds())
                .stream().map(StreamEntity::getStreamId).collect(Collectors.toList());
        if (request.getStreamIds().stream()
                .filter(id -> !existingStreamIds.contains(id)).count() > 0) {
            throw new InvalidRequestException("StreamEntity ids not present");
        }

        //TODO : add exam validation

        return CoachingInstituteDTO.builder()
                .instituteId(coachingInstituteService.update(request).getInstituteId()).build();
    }
}
