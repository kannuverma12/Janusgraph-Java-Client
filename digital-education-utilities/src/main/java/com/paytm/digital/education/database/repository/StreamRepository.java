package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamRepository extends MongoRepository<Stream, String> {

    Stream findByStreamId(Long id);

}
