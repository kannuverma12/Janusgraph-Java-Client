package com.paytm.digital.education.profiles.db.repository;

import com.paytm.digital.education.profiles.db.model.ProfileDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CandidateProfileDataRepository extends
        MongoRepository<ProfileDataEntity, Long> {

    List<ProfileDataEntity> findByProfileIdAndIsEnabled(Long id, Boolean isEnabled);

    List<ProfileDataEntity> findByProfileIdAndKeyInAndIsEnabled(Long id, List<String> keys, Boolean isEnabled);

    ProfileDataEntity findByProfileIdAndKey(Long id, String key);

    List<ProfileDataEntity> findByCustomerIdAndIsEnabled(Long id, Boolean isEnabled);

}
