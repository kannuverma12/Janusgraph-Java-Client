package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamRepository extends MongoRepository<Stream, String> {
    Stream findStreamByName(String name);
}
