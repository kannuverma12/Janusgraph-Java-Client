package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingStreamDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProducerStreamService {

    @Autowired
    private CoachingStreamDAO coachingStreamDAO;

    public StreamEntity create(StreamDataRequest request) {
        StreamEntity streamEntity = new StreamEntity();
        ConverterUtil.setStreamData(request, streamEntity);
        try {
            return coachingStreamDAO.save(streamEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public StreamEntity update(StreamDataRequest request) {
        StreamEntity existingStreamEntity =
                Optional.ofNullable(coachingStreamDAO.findByStreamId(request.getStreamId()))
                        .orElseThrow(() -> new InvalidRequestException(
                                "stream id not present : " + request.getStreamId()));
        ConverterUtil.setStreamData(request, existingStreamEntity);
        try {
            return coachingStreamDAO.save(existingStreamEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public StreamEntity findByStreamName(String name) {
        return coachingStreamDAO.findByStreamName(name);
    }

    public StreamEntity findByStreamId(Long instituteId) {
        return coachingStreamDAO.findByStreamId(instituteId);
    }

    public boolean isValidStreamIds(List<Long> ids) {
        List<Long> existingStreamIds = coachingStreamDAO.findAllByStreamId(ids)
                .stream().map(StreamEntity::getStreamId).collect(Collectors.toList());
        List<Long> invalidStreamIds = ids.stream().filter(id -> !existingStreamIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidStreamIds.isEmpty()) {
            throw new InvalidRequestException(
                    "Invalid StreamEntity ids given : " + invalidStreamIds);
        }
        return true;
    }
}
