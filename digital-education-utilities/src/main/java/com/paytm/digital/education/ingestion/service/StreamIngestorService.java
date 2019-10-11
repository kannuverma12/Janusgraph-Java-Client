package com.paytm.digital.education.ingestion.service;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.ingestion.dao.StreamDAO;
import com.paytm.digital.education.ingestion.request.StreamDataRequest;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StreamIngestorService {

    private static final Logger log = LoggerFactory.getLogger(StreamIngestorService.class);

    @Autowired
    private StreamDAO streamDAO;

    public StreamEntity createStream(StreamDataRequest request) {
        StreamEntity streamEntity = new StreamEntity();
        streamEntity.setCreatedAt(LocalDateTime.now());
        Optional.ofNullable(streamDAO.findByStreamName(request.getName()))
                .orElseThrow(() -> new InvalidRequestException(
                        "stream already exists : " + request.getName()));
        return saveStream(request, streamEntity);
    }

    public StreamEntity updateStream(StreamDataRequest request) {
        StreamEntity existingStreamEntity =
                Optional.ofNullable(streamDAO.findByStreamId(request.getStreamId()))
                        .orElseThrow(() -> new InvalidRequestException(
                                "stream id not present : " + request.getStreamId()));
        return saveStream(request, existingStreamEntity);
    }

    private StreamEntity saveStream(StreamDataRequest request, StreamEntity streamEntity) {
        setStreamData(request, streamEntity);
        try {
            return streamDAO.save(streamEntity);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error in saving stream data in db, exception : {}", ex);
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    private void setStreamData(StreamDataRequest request, StreamEntity streamEntity) {
        streamEntity.setLogo(request.getLogo());
        streamEntity.setName(request.getName());
        streamEntity.setPriority(request.getPriority());
        streamEntity.setIsEnabled(request.getIsEnabled());
        streamEntity.setUpdatedAt(LocalDateTime.now());
    }
}
