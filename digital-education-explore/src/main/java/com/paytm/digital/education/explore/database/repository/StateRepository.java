package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.database.entity.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends MongoRepository<State, String> {
    State findStateByName(String name);
}
