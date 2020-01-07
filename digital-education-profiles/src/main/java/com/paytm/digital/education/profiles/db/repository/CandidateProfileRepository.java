package com.paytm.digital.education.profiles.db.repository;

import com.paytm.digital.education.profiles.db.model.ProfileIdentifierEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CandidateProfileRepository extends MongoRepository<ProfileIdentifierEntity, Long> {

    ProfileIdentifierEntity findByProfileIdAndIsEnabled(Long id, Boolean isEnabled);

    ProfileIdentifierEntity findByProfileId(Long id);

    List<ProfileIdentifierEntity> findByCustomerId(Long id);

}
