package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.StreamDTO;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StreamManagerService {

    @Autowired
    private StreamService streamService;

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    public StreamDTO create(StreamDataRequest request) {

        List<CoachingInstituteEntity> existingCoachingInstitutes =
                coachingInstituteService.findAllByInstituteId(request.getTopInstitutes());
        if (request.getTopInstitutes().stream()
                .filter(id -> !existingCoachingInstitutes.contains(id)).count() > 0) {
            throw new InvalidRequestException("COACHING INSTITUTE NOT PRESET");
        }

        return StreamDTO.builder().streamId(streamService.create(request).getStreamId()).build();
    }

    public StreamDTO update(StreamDataRequest request) {
        Optional.ofNullable(request.getStreamId())
                .orElseThrow(() -> new InvalidRequestException("stream id should be present"));
        List<CoachingInstituteEntity> existingCoachingInstitutes =
                coachingInstituteService.findAllByInstituteId(request.getTopInstitutes());
        if (request.getTopInstitutes().stream()
                .filter(id -> !existingCoachingInstitutes.contains(id)).count() > 0) {
            throw new InvalidRequestException("COACHING INSTITUTE NOT PRESET");
        }

        return StreamDTO.builder().streamId(streamService.update(request).getStreamId()).build();
    }
}
