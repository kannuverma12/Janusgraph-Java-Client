package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.repository.StreamEntityRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StreamDAO {

    @Autowired
    private StreamEntityRepository streamRepository;

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

    @Cacheable(value = "stream_all", key = "'all_streams_entitys'")
    public List<StreamEntity> findAll() {
        return this.streamRepository.findAll();
    }

    public StreamEntity findByStreamName(@NonNull String name) {
        return streamRepository.findByStreamName(name);
    }

    @Cacheable(value = "streams_id_map", key = "'stream_entity_map'")
    public Map<Long, StreamEntity> getStreamEntityMapById() {
        List<StreamEntity> entities = this.streamRepository.findAll();
        return Optional.ofNullable(entities).map(streamEntities -> streamEntities.stream().collect(
                Collectors.toMap(s -> s.getStreamId(), s -> s))).orElse(new HashMap<>());
    }
}
