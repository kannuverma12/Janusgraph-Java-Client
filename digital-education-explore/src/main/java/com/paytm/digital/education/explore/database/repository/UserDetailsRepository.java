package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.database.entity.UserDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDetailsRepository extends MongoRepository<UserDetails, Long> {

    UserDetails getByUserId(Long id);

}
