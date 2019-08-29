package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingStreamDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StreamService {

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
                        .orElseThrow(() -> new ResourceNotFoundException("stream not present"));
        ConverterUtil.setStreamData(request, existingStreamEntity);
        return coachingStreamDAO.save(existingStreamEntity);
    }

    public List<StreamEntity> findAllByStreamId(List<Long> instituteIds) {
        return coachingStreamDAO.findAllByStreamId(instituteIds);
    }

    public StreamEntity findByStreamId(Long instituteId) {
        return coachingStreamDAO.findByStreamId(instituteId);
    }
}
