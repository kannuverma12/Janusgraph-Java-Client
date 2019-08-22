package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.db.dao.CoachingStreamDAO;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.database.entity.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Service
public class StreamService {

    @Autowired
    private CoachingStreamDAO coachingStreamDAO;

    public Stream create(StreamDataRequest request) {
        Stream stream = Stream.builder().logo(request.getLogo()).name(request.getName())
                .topInstitutes(request.getTopInstitutes()).build();

        stream.setIsEnabled(request.getIsEnabled());
        stream.setPriority(request.getPriority());
        return coachingStreamDAO.save(stream);
    }

    public Stream update(StreamDataRequest request) {

        Stream existingStream =
                Optional.ofNullable(coachingStreamDAO.findByStreamId(request.getStreamId()))
                        .orElseThrow(() -> new ResourceAccessException(
                                CoachingConstants.RESOURCE_NOT_PRESENT));

        Stream stream = Stream.builder().id(existingStream.getId()).logo(request.getLogo())
                .name(request.getName()).build();
        stream.setPriority(request.getPriority());
        stream.setIsEnabled(request.getIsEnabled());

        return coachingStreamDAO.save(stream);
    }
}
