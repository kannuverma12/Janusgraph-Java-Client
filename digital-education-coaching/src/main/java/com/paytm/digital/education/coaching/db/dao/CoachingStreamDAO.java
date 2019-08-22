package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.Stream;
import com.paytm.digital.education.database.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoachingStreamDAO {

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public Stream save(Stream stream) {
        stream.setStreamId(sequenceGenerator.getNextSequenceId(stream.getClass().getSimpleName()));
        return streamRepository.save(stream);
    }

    public Stream findByStreamId(Long id) {
        return streamRepository.findByStreamId(id);
    }
}
