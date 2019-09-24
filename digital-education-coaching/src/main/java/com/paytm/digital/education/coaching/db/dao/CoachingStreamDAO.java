package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.StreamRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingStreamDAO {

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public StreamEntity save(@NonNull StreamEntity streamEntity) {
        if (Objects.isNull(streamEntity.getStreamId())) {
            streamEntity.setStreamId(
                    sequenceGenerator.getNextSequenceId(streamEntity.getClass().getSimpleName()));
        }
        return streamRepository.save(streamEntity);
    }

    public StreamEntity findByStreamId(@NonNull Long id) {
        return streamRepository.findByStreamId(id);
    }

    public List<StreamEntity> findAllByStreamId(@NonNull List<Long> ids) {
        return streamRepository.findAllByStreamId(ids);
    }

    public List<StreamEntity> findAll() {
        return this.streamRepository.findAll();
    }

    public StreamEntity findByStreamName(@NonNull String name) {
        return streamRepository.findByStreamName(name);
    }
}
