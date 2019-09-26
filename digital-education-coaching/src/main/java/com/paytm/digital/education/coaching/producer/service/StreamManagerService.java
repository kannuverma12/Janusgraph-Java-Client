package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.StreamDTO;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class StreamManagerService {

    @Autowired
    private ProducerStreamService producerStreamService;

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    public StreamDTO create(StreamDataRequest request) {
        if (Objects.nonNull(request.getStreamId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getStreamId());
        }

        if (Objects.nonNull(producerStreamService.findByStreamName(request.getName()))) {
            throw new InvalidRequestException(
                    "name already present : " + request.getName());
        }

        return StreamDTO.builder().streamId(producerStreamService.create(request).getStreamId()).build();
    }

    public StreamDTO update(StreamDataRequest request) {
        Optional.ofNullable(request.getStreamId())
                .orElseThrow(() -> new InvalidRequestException("stream id should be present"));

        if (Objects.nonNull(producerStreamService.findByStreamName(request.getName()))) {
            throw new InvalidRequestException(
                    "name already present : " + request.getName());
        }
        return StreamDTO.builder().streamId(producerStreamService.update(request).getStreamId()).build();
    }
}
