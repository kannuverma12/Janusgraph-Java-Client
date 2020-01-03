package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.repository.StreamRepository;
import com.paytm.digital.education.metrics.annotations.NullValueAlert;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CoachingStreamDAO {

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

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

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public StreamEntity findByStreamId(String streamIdField, long streamId,
            List<String> projectionFields, List<String> mandatoryFields) {
        return commonMongoRepository.getEntityByFields(
                streamIdField, streamId, StreamEntity.class, projectionFields);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<StreamEntity> findByStreamIdsIn(String streamIdField, List<Long> streamIds,
            List<String> projectionFields, List<String> mandatoryFields) {
        return commonMongoRepository.getEntityFieldsByValuesIn(streamIdField, streamIds,
                StreamEntity.class, projectionFields);
    }

    public List<StreamEntity> findAllAndSortBy(Map<Sort.Direction, String> sortMap) {
        return commonMongoRepository.findAllAndSortBy(StreamEntity.class,
                Collections.EMPTY_LIST, sortMap);
    }
}
