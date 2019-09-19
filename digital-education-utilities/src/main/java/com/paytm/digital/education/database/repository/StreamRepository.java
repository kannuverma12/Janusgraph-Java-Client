package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.StreamEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamRepository extends MongoRepository<StreamEntity, ObjectId> {

    StreamEntity findByStreamId(Long id);

    @Query(value = "{stream_id: { $in: ?0 }}", fields = "{'stream_id':1, _id : 0}")
    List<StreamEntity> findAllByStreamId(List<Long> ids);

    @Query(value = "{'name': {$regex : '^?0$', $options: 'i'}}", fields = "{'stream_id':1, _id : 0}")
    StreamEntity findByStreamName(String name);

}
